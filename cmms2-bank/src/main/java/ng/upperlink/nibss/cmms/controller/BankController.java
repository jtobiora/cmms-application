package ng.upperlink.nibss.cmms.controller;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.bank.BankRequest;
import ng.upperlink.nibss.cmms.dto.bank.BankSearchRequest;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.enums.makerchecker.ViewAction;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.repo.SearchRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/bank")
@Slf4j
public class BankController {

    //private static Logger log = LoggerFactory.getLogger(BankUserController.class);

    private BankService bankService;
    private BankUserService bankUserService;
    private UserService userService;
    private SearchRepo searchRepo;
    private SmtpMailSender smtpMailSender;

    @Value("${email_from}")
    private String fromEmail;

    @Value("${encryption.salt}")
    private String salt;

    private UserType userType = UserType.BANK;
    private OtherAuthorizationService otherAuthorizationService;

    @Autowired
    public void setOtherAuthorizationService(OtherAuthorizationService otherAuthorizationService) {
        this.otherAuthorizationService = otherAuthorizationService;
    }

    private RoleService roleService;

    @Autowired
    public void setSearchRepo(SearchRepo searchRepo) {
        this.searchRepo = searchRepo;
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
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }

    @GetMapping({"/activated/{pageNumber}/{pageSize}"})
    public ResponseEntity getAllActivated(@RequestParam boolean activeStatus, @RequestParam Optional<Integer> pageNumber, @RequestParam Optional<Integer> pageSize) {

        try {
            if (pageNumber.isPresent() && pageSize.isPresent()) {
                int pNum = pageNumber.get();
                int pSize = pageSize.get();
                return ResponseEntity.ok(bankService.getAllActivated(activeStatus, new PageRequest(pNum, pSize)));
            } else

                return ResponseEntity.ok(bankService.getAllActivated(activeStatus));
        } catch (Exception e) {
            log.error("Banks could not be retrieved.",e);
            return new ResponseEntity<>(new ErrorDetails("Failed to load banks."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity getAll(@RequestParam int pageNumber, @RequestParam int pageSize) {
       try {
           return ResponseEntity.ok(bankService.getAll(new PageRequest(pageNumber, pageSize)));
       }catch (Exception e){
           log.error("Banks could not be retrieved.",e);
           return new ResponseEntity<>(new ErrorDetails("Failed to load banks."), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/searchByAnyKey")
    public ResponseEntity getAllByAnyKey(@RequestParam String anyKey, @RequestParam int pageNumber, @RequestParam int pageSize) {
        return ResponseEntity.ok(bankService.getBankByPropName(anyKey, new PageRequest(pageNumber, pageSize)));
    }

    @GetMapping("/{id}")
    public ResponseEntity getBankById(@PathVariable Long id) {
       try {
           return ResponseEntity.ok(bankService.getByBankId(id));
       }catch (Exception e){
           log.error("Bank with id {} could not be retrieved.",id,e);
           return new ResponseEntity<>(new ErrorDetails("Failed to load bank details."), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @PostMapping
    public ResponseEntity<?> createNewBank(@Valid @RequestBody BankRequest bankRequest,
                                           @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {

        try {

            return bankService.setup(bankRequest, userDetail, false, null, InitiatorActions.CREATE, null);
        } catch (CMMSException e) {
            return ErrorDetails.setUpErrors("Unable to create", Arrays.asList(e.getMessage()), e.getCode());
        }catch (Exception e){
            log.error("Unable to create banks.",e);
            return new ResponseEntity<>(new ErrorDetails("Failed to create bank."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity updateBank(@Valid @RequestBody BankRequest bankRequest,
                                     @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return bankService.setup(bankRequest, userDetail, true, null, InitiatorActions.UPDATE, null);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to update", Arrays.asList(e.getMessage()), e.getCode());
        }catch (Exception e){
            log.error("Unable to update banks.",e);
            return new ResponseEntity<>(new ErrorDetails("Failed to update bank."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/toggle")
    public ResponseEntity toggleBank(@Valid @RequestBody Id id,
                                     @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {

        try {
            return bankService.toggle(id.getId(), userDetail, false);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to toggle", Arrays.asList(e.getMessage()), e.getCode());
        }catch (Exception e){
            log.error("Unable to toggle bank.",e);
            return new ResponseEntity<>(new ErrorDetails("Unable to toggle bank."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/toggle/emandate")
    public ResponseEntity toggleBankEmandate(@Valid @RequestBody Id id,
                                             @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {

        try {
            return bankService.toggle(id.getId(), userDetail, true);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to toggle e-mandate", Arrays.asList(e.getMessage()), e.getCode());
        }catch (Exception e){
            log.error("Unable to toggle emandate.",e);
            return new ResponseEntity<>(new ErrorDetails("Unable to toggle emandate."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendApprovalEmail(String bankCode, Bank bank, boolean isUpdate) {

        List<String> emailAddress = bankUserService.getAllActiveAuthorizerEmailAddress(bank.getId());

        System.out.println(emailAddress);

        if (!emailAddress.isEmpty()) {

            String message = "A Bank " + bank.getName() + " was created. Please log on to the portal to approve or disapprove the bank";
            if (isUpdate) {
                message = "The Bank " + bank.getName() + " was updated. Please log on to the portal to approve or disapprove the update";
            }

            //Send the mail

            smtpMailSender.sendMail(fromEmail, emailAddress.toArray(new String[emailAddress.size()]), "User Approval awareness",
                    "Bank Approval Awareness", message, generateDetails(bank, isUpdate));
        }
    }

    private void sendAwarenessEmail(Bank bank, boolean isUpdated, boolean isApproved) {


        if (bank.getCreatedBy() == null) {
            log.error("BANK 'created by' is NULL");
            return;
        }

        if (isUpdated && bank.getUpdatedBy() == null) {
            log.error("BANK 'updated by' is NULL");
            return;
        }

        String emailAddress = bank.getCreatedBy().getEmailAddress();
        if (isUpdated) {
            emailAddress = bank.getUpdatedBy().getEmailAddress();
        }

        String[] email = {emailAddress};

        if (email.length > 0) {

            String message = "The Bank " + bank.getName() + " " + bank.getCode() + " have been " + (isApproved ? "approved" : "disapproved");
            if (isUpdated) {
                message = "The changes on the Bank " + bank.getCode() + " " + bank.getCode() + " have been " + (isApproved ? "approved" : "disapproved");
            }

            //Send the mail
            smtpMailSender.sendMail(fromEmail, email, "Bank Approval awareness",
                    "Bank Approval Awareness", message, generateDetails(bank, isUpdated));
        }

    }

    @PostMapping("/search")
    public ResponseEntity searchBank(@RequestBody BankSearchRequest bankSearchRequest,
                                     Pageable pageable) {

//          boolean flag = StringUtils.isEmpty(bankSearchRequest.getActivated()) ? true : false;
//
//          boolean status = Boolean.parseBoolean(bankSearchRequest.getActivated());
//
//          return ResponseEntity.ok(bankService.searchBankEntity(bankSearchRequest.getCode(),
//                  bankSearchRequest.getName(),bankSearchRequest.getNipBankCode(),status,flag,pageable));

        try {
            return searchRepo.searchBank(bankSearchRequest.getCode(),
                    bankSearchRequest.getName(), bankSearchRequest.getNipBankCode(), bankSearchRequest.getActivated(), pageable);

        }catch (Exception e){
            log.error("Unable to complete search operation.",e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
           }


    private String generateDetails(Bank bank, boolean isUpdate) {
        String details = "";

        details += "<strong>Name :</strong> " + bank.getName() + "<br/>";
        details += "<strong>Code :</strong> " + bank.getCode() + "<br/>";

        return details;
    }

    @GetMapping("/viewAction")
    public ResponseEntity getAllPendingUsers(@RequestParam ViewAction viewAction, @RequestParam int pageNumber, @RequestParam int pageSize) {
       try {
           return ResponseEntity.ok(bankService.selectView(viewAction, new PageRequest(pageNumber, pageSize)));
       }catch(Exception e){
           log.error("Operation failed.",e);
           return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/previewUpdate")
    public ResponseEntity previewUpdate(@RequestParam Long id,
                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return ResponseEntity.ok(bankService.previewUpdate(id));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to preview", Arrays.asList(e.getMessage()), e.getCode());
        }catch(Exception e){
            log.error("Preview Update Operation failed.",e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //return ResponseEntity.status(201).body(bankService.previewUpdate(id));
    }

    @PutMapping("/authorization")
    public ResponseEntity authorizationAction(@Valid @RequestBody AuthorizationRequest request, @RequestParam AuthorizationAction action,
                                              @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        Bank bank = null;
        try {
            bank = (Bank) otherAuthorizationService.performAuthorization(request, action, userDetail, EntityType.BANK);
            return ResponseEntity.status(200).body(bank);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Authorization failed", Arrays.asList("Authorization failed"), e.getCode());
        }catch(Exception e){
            log.error("Authorization failed.",e);
            return new ResponseEntity<>(new ErrorDetails(new Date(),"Authorization failed.","Authorization failed."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}