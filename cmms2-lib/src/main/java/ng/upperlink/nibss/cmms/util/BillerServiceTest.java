package ng.upperlink.nibss.cmms.util;

import ng.upperlink.nibss.cmms.dto.biller.BillerRequest;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.Industry;
import ng.upperlink.nibss.cmms.repo.biller.BillerRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.biller.CompanyService;
import ng.upperlink.nibss.cmms.service.biller.IndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class BillerServiceTest {
//
//
//    //    private static Logger LOG = LoggerFactory.getLogger(BankService.class);
//    private BillerRepo billerRepo;
//
//    private UserService userService;
//    private BankService bankService ;
//
//    private IndustryService industryService;
//    private CompanyService companyService;
//    private BillerService billerService ;
//
//    @Autowired
//    public void setBillerService (BillerService billerService){this.billerService = billerService;}
//
//    @Autowired
//    public void setIndustryService (IndustryService industryService){this.industryService = industryService;}
//
//    @Autowired
//    public void setBankService(BankService bankService){
//        this.bankService = bankService;
//    }
//    @Autowired
//    public void setBillerRepo(BillerRepo billerRepo) {
//        this.billerRepo = billerRepo;
//    }
//
//    @Autowired
//    public void setCompanyService(CompanyService companyService) {
//        this.companyService = companyService;
//    }
//
//    @Autowired
//    public void setUserService(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Transactional
//    public Biller save(Biller biller, Bank bank, boolean isBankAsBiller) {
//        biller = billerRepo.save(biller);
//        if (bank !=null && isBankAsBiller){
//            Set<Biller> billers = Collections.singleton(biller);
//            bank.setOwnedBillers(billers);
//            bankService.save(bank);
//        }
//        return  biller;
//    }
//
//    public Page<Biller> getAllActive(boolean activeStatus, Pageable pageable) {
//        return billerRepo.getAllActiveStatus(activeStatus, pageable);
//    }
//
//    public List<Biller> getAllActive(boolean activeStatus) {
//        return billerRepo.getAllActiveStatus(activeStatus);
//    }
//
//    public Page<Biller> getAll(Pageable pageable) {
//        return billerRepo.findAll(pageable);
//    }
//
//    public  long countAccountNumber(String accountNumber){
//        return billerRepo.countAccountNumber(accountNumber);
//    }
//
//    public Biller getBillerById(Long id) {
//        return billerRepo.findOne(id);
//    }
//
//    public long getCountOfSameRCNumber(String rcNumber, Long id) {
//        if (id == null) {
//            return companyService.countOfSameRCNumber(rcNumber);
//        }
//        return companyService.countOfSameRCNumber(rcNumber, id);
//    }
//
//    public Biller toggleBiller(Long id, User user) {
//        Biller billerToBeToggled = this.getBillerById(id);
//        if (billerToBeToggled == null) {
//            return new Biller();
//        } else {
//            boolean activated = billerToBeToggled.isActivated() ;
//            billerToBeToggled.setUpdatedBy(user);
//            billerToBeToggled.setActivated(!activated);
//            billerToBeToggled = this.save(billerToBeToggled,null, false);
//            return billerToBeToggled;
//        }
//    }
//
//    public String validate(BillerRequest request, boolean isUpdate, Long id) {
//
//        long rcNumberCount = 0;
//        long accountNumberCount = 0;
//        if (isUpdate) {
//            if (id == null) {
//                return "Biller id is not provided";
//            }
//            rcNumberCount = getCountOfSameRCNumber(request.getRcNumber(), request.getId());
//        } else {
//
//            if (request.getRcNumber() == null || request.getRcNumber().isEmpty()) {
//                return "Biller RcNumber is not provided";
//            }
//            rcNumberCount = companyService.countOfSameRCNumber(request.getRcNumber());
//
//            if (request.getAccountNumber()==null || request.getAccountNumber().isEmpty()){
//                return "Biller bank account number is not provided";
//            }
//            accountNumberCount = countAccountNumber(request.getAccountNumber());
//        }
//        if (rcNumberCount > 0) {
//            return "This RcNumber '" + request.getRcNumber() + "' already exist";
//
//        }
//        if (accountNumberCount > 0) {
//            return "This account number '" + request.getAccountNumber() + "' already exist";
//
//        }
//        return null;
//    }
//
//    public Biller generate(Biller biller, BillerRequest request, User operator, boolean isUpdate) {
//        biller.setDescription(request.getDescription());
//        biller.setName(request.getName());
//        biller.setIndustry(request.getIndustry());
//        if (isUpdate) {
//            biller.setId(request.getId());
//            if (operator != null)
//                biller.setUpdatedAt(new Date());
//            biller.setUpdatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.isActivated(), operator.getEntityType()));
//
//        } else {
//            biller.setAccountName(request.getAccountName());
//            biller.setAccountNumber(request.getAccountNumber());
//            biller.setRcNumber(request.getRcNumber());
//            biller.setCreatedAt(new Date());
//            biller.setBvn(request.getBvn());
//            biller.setActivated(true);
//            biller.setBank(request.getBank());
//            biller.setCreatedAt(new Date());
//            if (operator != null) {
//                biller.setCreatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.isActivated(), operator.getEntityType()));
//
//            }
//        }
//        return biller;
//    }
//
//    public ResponseEntity<?> setup(BillerRequest billerRequest, User operator , boolean isUpdate){
//
//        String errorResult = this.validate(billerRequest, isUpdate, billerRequest.getId());
//        if (errorResult != null){
//            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
//        }
//        Industry industry = industryService.getOne(billerRequest.getIndustryId());
//        if (industry ==null){
//            return ResponseEntity.badRequest().body(new ErrorDetails("Industry not found"));
//
//        }
//        billerRequest.setIndustry(industry);
//        Bank billerBank = null;
//        Biller billerToBeSaved =null;
//        if (isUpdate){
//            billerToBeSaved = billerService.getBillerById(billerRequest.getId());
//
//            if(billerToBeSaved ==null){
//                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_IS_NULL .getValue().replace("{}","Biller to be updated ")));
//            }
//            billerToBeSaved = generate(billerToBeSaved, billerRequest, operator, true);
//
//        }else {
//            billerBank = bankService.getByBankId(billerRequest.getBankId());
//            if (billerBank == null) {
//                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_IS_NULL.getValue().replace("{}", "Biller bank ")));
//            }
//            billerRequest.setBank(billerBank);
//            billerToBeSaved = generate(new Biller(), billerRequest, operator, false);
//        }
//        billerToBeSaved = save(billerToBeSaved ,billerBank,billerRequest.isBankAsBiller());
//
//        return ResponseEntity.ok(billerToBeSaved);
//    }
}
