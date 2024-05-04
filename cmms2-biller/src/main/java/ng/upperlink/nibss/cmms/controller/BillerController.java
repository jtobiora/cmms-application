package ng.upperlink.nibss.cmms.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.BillerRequest;
import ng.upperlink.nibss.cmms.dto.biller.BillerSearchRequest;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.enums.makerchecker.ViewAction;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.repo.SearchRepo;
import ng.upperlink.nibss.cmms.repo.biller.BeneficiaryRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.*;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import ng.upperlink.nibss.cmms.service.nibss.NibssUserService;
import ng.upperlink.nibss.cmms.util.AccountLookUp;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/biller")
@Api(value = "BillersResource")
public class BillerController {
    // private static Logger log = LoggerFactory.getLogger(BillerController.class);

    private BillerService billerService;
    private BankService bankService;
    private BankUserService bankUserService;
    private NibssUserService nibssUserService;
    private UserService userService;
    private IndustryService industryService;
    private FeeService feeService;
    private BeneficiaryService beneficiaryService;
    private BeneficiaryRepo beneficiaryRepo;
    private SmtpMailSender smtpMailSender;
    private SharingFormularService sharingFormularService;
    private AccountLookUp accountLookUp;
    private SearchRepo searchRepo;

    @Value("${email_from}")
    private String fromEmail;

    @Value("${encryption.salt}")
    private String salt;

    private UserType userType = UserType.BANK;

    private RoleService roleService;

    private OtherAuthorizationService otherAuthorizationService;

    @Autowired
    public void setOtherAuthorizationService(OtherAuthorizationService otherAuthorizationService) {
        this.otherAuthorizationService = otherAuthorizationService;
    }

    @Autowired
    public void setSearchRepo(SearchRepo searchRepo) {
        this.searchRepo = searchRepo;
    }

    @Autowired
    public void setAccountLookUp(AccountLookUp accountLookUp) {
        this.accountLookUp = accountLookUp;
    }

    @Autowired
    public void setSharingFormularService(SharingFormularService sharingFormularService) {
        this.sharingFormularService = sharingFormularService;
    }

    @Autowired
    public void setBeneficiaryRepo(BeneficiaryRepo beneficiaryRepo) {
        this.beneficiaryRepo = beneficiaryRepo;
    }

    @Autowired
    public void setBeneficiaryService(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @Autowired
    public void setFeeService(FeeService feeService) {
        this.feeService = feeService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setBankUserService(BankUserService bankUserService) {
        this.bankUserService = bankUserService;
    }

    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setIndustryService(IndustryService industryService) {
        this.industryService = industryService;
    }

    @Autowired
    public void setBillerService(BillerService billerService) {
        this.billerService = billerService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setNibssUserService(NibssUserService nibssUserService) {
        this.nibssUserService = nibssUserService;
    }

    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }

    @GetMapping({"/activeStatus"})
    public ResponseEntity getAllActive(@RequestParam boolean activeStatus, @RequestParam(required = false) Integer pageNumber, @RequestParam(required = false) Integer pageSize) {
        try {

            if (pageNumber != null && pageSize != null) {

                return ResponseEntity.ok(billerService.getAllActive(activeStatus, new PageRequest(pageNumber, pageSize)));
            } else
                return ResponseEntity.ok(billerService.getAllActive(activeStatus));
        } catch (Exception e) {
            log.error("Could not fetch all billers with their status ", e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bankId")
    public ResponseEntity getAllBillerByBankId(@RequestParam boolean status, @RequestParam(required = false) Integer pageNumber, @RequestParam(required = false) Integer pageSize,
                                               @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            if (pageNumber != null && pageSize != null) {
                return ResponseEntity.ok(billerService.getAllByBillerOwner(status, userDetail, new PageRequest(pageNumber, pageSize)));
            } else

                return ResponseEntity.ok(billerService.getAllByBillerOwner(status, userDetail));

        } catch (CMMSException e) {
            return ErrorDetails.setUpErrors("Unable to retrieve", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Could not fetch all billers by banks ", e);
            return new ResponseEntity<>(new ErrorDetails("Could not fetch all billers."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/onboarded")
    public ResponseEntity getRecruitedBillers(@RequestParam(required = false) Integer pageNumber, @RequestParam(required = false) Integer pageSize,
                                              @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            if (pageNumber != null && pageSize != null) {
                return ResponseEntity.ok(billerService.getAllByBillerOwner(userDetail, new PageRequest(pageNumber, pageSize)));
            } else

                return ResponseEntity.ok(billerService.getAllByBillerOwner(userDetail));

        } catch (CMMSException e) {
            return ErrorDetails.setUpErrors("Unable to retrieve", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Exception thrown while retrieving list of onboarded billers", e);
            return new ResponseEntity<>(new ErrorDetails("Could not retrieve list of onboarded billers."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/activated")
    public ResponseEntity getAllActive(@RequestParam boolean activeStatus) {
        try {
            return ResponseEntity.ok(billerService.getAllActive(activeStatus));
        } catch (Exception e) {
            log.error("Exception thrown while fetching billers and their statuses", e);
            return new ResponseEntity<>(new ErrorDetails("Unable to retrieve list of billers."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/recruited")
    public ResponseEntity<?> getByOwner(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return billerService.loggedUser(userDetail);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to retrieve", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception ex) {
            log.error("Exception thrown while retrieving list of onboarded billers", ex);
            return new ResponseEntity<>(new ErrorDetails("Could not retrieve list of onboarded billers"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity getAll(@RequestParam int pageNumber, @RequestParam int pageSize) {
        try {
            return ResponseEntity.ok(billerService.getAllBiller(new PageRequest(pageNumber, pageSize)));
        } catch (Exception ex) {
            log.error("Exception thrown while retrieving all billers", ex);
            return new ResponseEntity<>(new ErrorDetails("Unable to fetch all billers."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getBillerById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(billerService.getBillerById(id));
        } catch (Exception e) {
            log.error("Unable to retieve biller with id {} ", id, e);
            return new ResponseEntity<>(new ErrorDetails("Unable to get biller details."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createNewBiller(@Valid @RequestBody BillerRequest billerRequest,
                                             @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {


        String result = accountLookUp.validateAccount(billerRequest.getAccountNumber(), billerRequest.getBvn());
        if (result != null) {
            return ErrorDetails.setUpErrors("Unable to create", Arrays.asList(result), "400");
        }
        try {
            return billerService.setup(billerRequest, false, userDetail, null, InitiatorActions.CREATE);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to create", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Biller creation failed ", e);
            return new ResponseEntity<>(new ErrorDetails("Failed to create a new biller. Please try again."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity updateBiller(@Valid @RequestBody BillerRequest billerRequest,
                                       @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {


        String result = accountLookUp.validateAccount(billerRequest.getAccountNumber(), billerRequest.getBvn());
        if (result != null) {
            return ErrorDetails.setUpErrors("Unable to update", Arrays.asList(result), "400");
        }
        try {
            return ResponseEntity.ok(billerService.setup(billerRequest, true, userDetail, null, InitiatorActions.UPDATE));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to update", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Updates failed ", e);
            return new ResponseEntity<>(new ErrorDetails("Biller details were not updated. Please try again."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/toggle")
    public ResponseEntity toggleBiller(@Valid @RequestBody Id id,
                                       @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return billerService.toggleInit(id.getId(), userDetail);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to toggle", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Exception thrown while toggling biller information", e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
//
//    @PutMapping("/toggle/emandate")
//    public ResponseEntity toggleBillerEmandate(@Valid @RequestBody Id id,
//                                               @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//
//        try {
//            return billerService.toggleInit(id.getId(), userDetail);
//        } catch (CMMSException e) {
//            e.printStackTrace();
//            return ErrorDetails.setUpErrors("Unable to toggle e-mandate", Arrays.asList(e.getMessage()),e.getCode());
//        }catch(Exception e){
//            log.error("Exception thrown while toggling emandate",e);
//            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/viewAction")
    public ResponseEntity getAllPendingBillers(@RequestParam ViewAction viewAction, @RequestParam int pageNumber, @RequestParam int pageSize,
                                               @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return ResponseEntity.ok(billerService.selectView(userDetail, viewAction, new PageRequest(pageNumber, pageSize)));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("View failed", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Exception thrown while processing information for all pending billers", e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/previewUpdate")
    public ResponseEntity previewUpdate(@RequestParam Long id,
                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return ResponseEntity.status(201).body(billerService.previewUpdate(id));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to preview", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Preview failed ", e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/authorization")
    public ResponseEntity authorizationAction(@Valid @RequestBody AuthorizationRequest request, @RequestParam AuthorizationAction action,
                                              @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        Biller biller = null;
        try {
            biller = (Biller) otherAuthorizationService.performAuthorization(request, action, userDetail, EntityType.BILLER);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Authorization failed:", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Authorization failed ", e);
            return new ResponseEntity<>(new ErrorDetails("Authorization was not successful."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(200).body(biller);
    }

    @PostMapping("/search")
    public ResponseEntity<Object> searchBillers(@Valid @RequestBody BillerSearchRequest request,
                                                Pageable pageable) {

        try {
            return searchRepo.searchBiller(request.getAccountNumber(), request.getAccountName(), request.getCode(),
                    request.getCompanyName(), request.getDescription(), request.getRcNumber(),
                    request.getActivated(), request.getBvn(), pageable);
        } catch (Exception e) {
            log.error("Biller search was not successful. ", e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
