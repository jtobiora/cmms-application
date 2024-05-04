package ng.upperlink.nibss.cmms.service.biller;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.FeesRequest;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.mandate.Fee;
import ng.upperlink.nibss.cmms.repo.biller.FeeRepo;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.util.AccountLookUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class FeeService {
    private FeeRepo feeRepo;
    private BillerService billerService;
    private BankService bankService;
    private AccountLookUp accountLookUp;

    @Value("${cmms.fee}")
    private String cmmsDefaultFee;


    @Autowired
    public void setAccountLookUp(AccountLookUp accountLookUp) {
        this.accountLookUp = accountLookUp;
    }

    @Autowired
    public void setBankService(BankService bankService){
        this.bankService = bankService;
    }

    @Autowired
    public void setBillerService(BillerService billerService){
        this.billerService = billerService;
    }

    @Autowired
    public void setFeeRepo(FeeRepo feeRepo){
        this.feeRepo = feeRepo;
    }


    public Fee getFeeConfigByBillerId(Long billerId){
        return feeRepo.findFeeConfigByBillerId(billerId);
    }

    public Fee getFeeConfigById(Long feeId){
        return feeRepo.findFeeConfigById(feeId);
    }

    public ResponseEntity processFees(FeesRequest feesRequest,boolean isUpdate,UserDetail userDetail){
        try{

            //validate user permisions to configure fee
            Map<String, String> userPermit = billerService.validateUserPermissions(userDetail, new String[]{RoleName.BANK_ADMIN_INITIATOR.getValue(), RoleName.NIBSS_INITIATOR.getValue(),RoleName.PSSP_ADMIN_INITIATOR.getValue()});

            if (!userPermit.get("message").equals("")) {
                return ResponseEntity.badRequest().body(new ErrorDetails(userPermit.get("message")));
            }

            Fee feeInDb = this.getFeeConfigByBillerId(feesRequest.getBillerId());
            Fee fee = null;
            Biller biller = null;

            if (isUpdate) {
                fee = feeInDb;
                biller = fee.getBiller();
            } else {
                      //verify if the fee has already been set up before
                if (feeInDb != null) {
                    return ResponseEntity.badRequest().body(new ErrorDetails("Fee configuration has already been done for this biller"));
                }

                fee = new Fee();

                //get the biller from request
                biller = billerService.getBillerById(feesRequest.getBillerId());

                if (biller == null) {
                    return ResponseEntity.badRequest().body(new ErrorDetails("Biller could not be found!"));
                }
            }

//                    //markup fee is checked
            if(feesRequest.isMarkUpFeeSelected()){
                     //FeeBearer must be selected
                String validatedRequest = this.validateRequest(feesRequest);
                if (validatedRequest != null) {
                    return ResponseEntity.badRequest().body(new ErrorDetails(validatedRequest));
                }

                FeeBearer feeBearer = FeeBearer.find(feesRequest.getFeeBearer());

                switch(feeBearer){
                    case NONE:
                        return ResponseEntity.badRequest().body(new ErrorDetails("A fee bearer must be selected to continue"));
                    case BILLER:
                        if (feesRequest.isDebitAtTransactionTime()) {
                            if (StringUtils.isEmpty(feesRequest.getBillerDebitAccountNumber())) {
                                return ResponseEntity.badRequest().body(new ErrorDetails("Please enter the biller's account number to continue!"));
                            }

                            if (!accountLookUp.validateAccount(feesRequest.getBillerDebitAccountNumber(), Constants.ACC_NUMBER_MAX_DIGITS)) {
                                return ResponseEntity.badRequest().body(new ErrorDetails(String.format("Account number must be digits and not less than %d characters", Constants.ACC_NUMBER_MAX_DIGITS)));
                            }
                        }
                        break;
                    case BANK:
                        break;
                    case SUBSCRIBER:
                        break;
                    default:
                }

                         //set the beneficiary bank details
                Bank bank = bankService.getBankByCode(feesRequest.getBankCode());

                if (bank == null) {
                    return ResponseEntity.badRequest().body(new ErrorDetails("Bank of the beneficiary not found!"));
                }

                if (!accountLookUp.validateAccount(feesRequest.getAccountNumber(), Constants.ACC_NUMBER_MAX_DIGITS)) {
                    return ResponseEntity.badRequest().body(new ErrorDetails(String.format("Account number must be digits and not less than %d characters", Constants.ACC_NUMBER_MAX_DIGITS)));
                }

                fee.setFeeBearer(FeeBearer.find(feesRequest.getFeeBearer()));
                fee.setSplitType(SplitType.FIXED);
                fee.setDefaultFee(new BigDecimal(cmmsDefaultFee));
                fee.setBiller(new Biller(biller.getId(),biller.getName(),biller.getRcNumber(),biller.getAccountNumber(),biller.getDescription(),
                        biller.getAccountName()));
                fee.setBank(new Bank(bank.getId(),bank.getCode(),bank.getName(),bank.getApiKey()));
                fee.setMarkUpFee(feesRequest.getMarkUpFee());  //the mark up fee
                fee.setDebitAtTransactionTime(feesRequest.isDebitAtTransactionTime());
                fee.setBillerDebitAccountNumber(feesRequest.getBillerDebitAccountNumber());
                fee.setAccountName(feesRequest.getAccountName());
                fee.setAccountNumber(feesRequest.getAccountNumber());
                fee.setMarkedUp(true);
            } else {
                fee.setBiller(new Biller(biller.getId(),biller.getName(),biller.getRcNumber(),biller.getAccountNumber(),biller.getDescription(),
                        biller.getAccountName()));
                fee.setDefaultFee(new BigDecimal(cmmsDefaultFee));
                fee.setFeeBearer(FeeBearer.NONE);
                fee.setSplitType(SplitType.NONE);
                fee.setMarkedUp(false);
                fee.setAccountNumber(null);
                fee.setAccountName(null);
                fee.setBillerDebitAccountName(null);
                fee.setBillerDebitAccountNumber(null);
                fee.setDebitAtTransactionTime(false);
            }

            return ResponseEntity.ok(saveFee(fee));
        }catch(Exception ex){
            log.error("Biller fee set up could not be completed",ex);
            return new ResponseEntity<>(new ErrorDetails("Biller fee set up could not be completed."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public List<Fee> findAllBillerFeesConfigured(){
        return feeRepo.findAllBillerFeeConfig();
    }

    public Page<Fee> findAllFees(Pageable pageable){
        return feeRepo.findAllBillerFeeConfig(pageable);
    }

    public Fee saveFee(Fee fee){
        return feeRepo.save(fee);
    }

    public String validateRequest(FeesRequest request) {
//        if (StringUtils.isEmpty(request.getSplitType())) {
//            return "Split type cannot be empty!";
//        }

        if (org.apache.commons.lang.StringUtils.isEmpty(request.getFeeBearer())) {
            return "Fee Bearer cannot be empty!";
        }

        return null;
    }

}
