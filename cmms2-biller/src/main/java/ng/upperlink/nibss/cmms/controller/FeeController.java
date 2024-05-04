package ng.upperlink.nibss.cmms.controller;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.FeeSearchRequest;
import ng.upperlink.nibss.cmms.dto.biller.FeesRequest;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.mandate.Fee;
import ng.upperlink.nibss.cmms.repo.SearchRepo;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.biller.FeeService;
import ng.upperlink.nibss.cmms.util.AccountLookUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;


@Slf4j
@RestController
@RequestMapping("/fees")
public class FeeController {
    private BillerService billerService;
    private FeeService feeService;
    @Value("${cmms.fee}")
    private String cmmsDefaultFee;
    private AccountLookUp accountLookUp;
    private SearchRepo searchRepo;
    private BankService bankService;


    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setSearchRepo(SearchRepo searchRepo) {
        this.searchRepo = searchRepo;
    }


    @Autowired
    public void setFeeService(FeeService feeService) {
        this.feeService = feeService;
    }

    @Autowired
    public void setAccountLookUp(AccountLookUp accountLookUp) {
        this.accountLookUp = accountLookUp;
    }

    @Autowired
    public void setBillerService(BillerService billerService) {
        this.billerService = billerService;
    }

    @PostMapping
    public ResponseEntity setUpFee(@Valid @RequestBody FeesRequest feesRequest,
                                   @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        return feeService.processFees(feesRequest, false, userDetail);
    }

    @PutMapping
    public ResponseEntity updateFeeConfig(@Valid @RequestBody FeesRequest feesRequest,
                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {

        return feeService.processFees(feesRequest, true, userDetail);
    }

    @GetMapping
    public ResponseEntity getAllBillerFeeConfig(Pageable pageable) {
        try {
            return ResponseEntity.ok(feeService.findAllFees(pageable));
        } catch (Exception e) {
            log.error("Could not load all configured fees", e);
            return new ResponseEntity<>(new ErrorDetails("Failed to load data."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{billerId}")
    public ResponseEntity getBillerFeeById(@PathVariable("billerId") Long billerId) {
        try {

            if (billerId == null) {
                return new ResponseEntity<>(new ErrorDetails("Biller id cannot be null!"), HttpStatus.BAD_REQUEST);
            }

            Fee fee = feeService.getFeeConfigByBillerId(billerId);
            if (fee == null) {
                //fee has not been configured for this biller
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }

            return ResponseEntity.ok(fee);
        } catch (Exception e) {
            log.error("Unable to load fees configured for biller with id {}",billerId,e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Object> searchFees(@Valid @RequestBody FeeSearchRequest request,
                                             Pageable pageable) {

       try {
           return searchRepo.searchFees(request.getMarkUpFee(), request.getFeeBearer(), request.getBillerAccNumber(),
                   request.getBillerName(), request.getDebitAtTrans(), request.getMarkUpFeeSelected(), request.getBeneficiaryBankCode(),
                   pageable);
       }catch(Exception e){
           log.error("Search could not be completed",e);
           return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

}
