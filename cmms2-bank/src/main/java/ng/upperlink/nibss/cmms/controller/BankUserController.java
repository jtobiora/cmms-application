package ng.upperlink.nibss.cmms.controller;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.bank.BankLoginDetails;
import ng.upperlink.nibss.cmms.dto.bank.BankUserRequest;
import ng.upperlink.nibss.cmms.dto.search.UsersSearchRequest;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.enums.makerchecker.ViewAction;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.repo.SearchRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.makerchecker.UserAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.ws.rs.HEAD;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


//@ApiIgnore
@RestController
@RequestMapping("/user/bank")
@Slf4j
public class BankUserController {

    // private static Logger LOG = LoggerFactory.getLogger(BankUserController.class);

    private BankUserService bankUserService;
    private BankService bankService;
    private UserService userService;
    private SearchRepo searchRepo;
    private UserAuthorizationService authorizationService;

    @Value("${email_from}")
    private String fromEmail;

    @Value("${encryption.salt}")
    private String salt;

    private UserType userType = UserType.BANK;

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
    public void setAuthorizationService(UserAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity getAllByBankId(@RequestParam Long bankId, @RequestParam int pageNumber, @RequestParam int pageSize) {
        try {
            return ResponseEntity.ok(bankUserService.getAllByBankId(bankId, new PageRequest(pageNumber, pageSize)));
        } catch (Exception e) {
            log.error("Bank user could not be retrieved.", e);
            return new ResponseEntity<>(new ErrorDetails("Failed to load users."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getBankUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bankUserService.getById(id));
        } catch (Exception e) {
            log.error("User with id {} could not be retrieved.", id, e);
            return new ResponseEntity<>(new ErrorDetails("Failed to load user details."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/viewAction")
    public ResponseEntity getAllPendingUsers(@RequestParam Long bankId, @RequestParam ViewAction viewAction, @RequestParam int pageNumber, @RequestParam int pageSize) {
        try {
            return ResponseEntity.ok(bankUserService.selectView(bankId, viewAction, new PageRequest(pageNumber, pageSize)));
        } catch (Exception e) {
            log.error("operation failed.", e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/previewUpdate")
    public ResponseEntity previewUpdate(@RequestParam Long id,
                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return ResponseEntity.status(201).body(bankUserService.previewUpdate(id));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to preview", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Unable to preview.", e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity getAll(@RequestParam Optional<Integer> pageNumber, @RequestParam Optional<Integer> pageSize) {
        try {
            if (pageNumber.isPresent() && pageSize.isPresent()) {
                int pNum = pageNumber.get();
                int pSize = pageSize.get();
                return ResponseEntity.ok(bankUserService.getAll(new PageRequest(pNum, pSize)));
            } else
                return ResponseEntity.ok(bankUserService.getAll());

        } catch (Exception e) {
            log.error("Bank users could not be retrieved.", e);
            return new ResponseEntity<>(new ErrorDetails("Failed to load users."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active/{bankId}")
    public ResponseEntity getAllByStatusAndBankId(@PathVariable(value = "bankId") Long bankId, @RequestParam boolean status, @RequestParam int pageNum, @RequestParam int pageSize) {
        try {
            return ResponseEntity.ok(bankUserService.getAllByStatusAndBankId(status, bankId, new PageRequest(pageNum, pageSize)));
        } catch (Exception e) {
            log.error("Activated users could not be retrieved.", e);
            return new ResponseEntity<>(new ErrorDetails("Failed to load users."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping({"/active"})
    public ResponseEntity getAllByStatus(@RequestParam Long bankId, @RequestParam boolean status, @RequestParam Optional<Integer> pageNumber, @RequestParam Optional<Integer> pageSize) {
        try {
            if (pageNumber.isPresent() && pageSize.isPresent()) {
                int pNum = pageNumber.get();
                int pSize = pageSize.get();
                return ResponseEntity.ok(bankUserService.getAllByActiveStatus(bankId, status, new PageRequest(pNum, pSize)));
            } else

                return ResponseEntity.ok(bankUserService.getAllByActiveStatus(bankId, status));

        } catch (Exception e) {
            log.error("Activated users could not be retrieved.", e);
            return new ResponseEntity<>(new ErrorDetails("Failed to load users."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody BankUserRequest bankUserRequest,
                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {

        User operatorUser = userService.get(userDetail.getUserId());

        if (operatorUser == null) {
            return ErrorDetails.setUpErrors("Unable to create", Arrays.asList(Errors.UNKNOWN_USER.getValue()), "404");
        }
        try {
            bankUserService.authenticate(bankUserRequest.getRoleId(), operatorUser, null, InitiatorActions.CREATE);
            return bankUserService.setup(bankUserRequest, operatorUser, false);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to create", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Unable to create user.", e);
            return new ResponseEntity<>(new ErrorDetails("Failed to create user."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity updateUser(@RequestBody BankUserRequest bankUserRequest,
                                     @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {

        User operatorUser = userService.get(userDetail.getUserId());

        if (operatorUser == null) {
            return ErrorDetails.setUpErrors("Unable to update", Arrays.asList(Errors.UNKNOWN_USER.getValue()), "404");
        }
        try {
            bankUserService.authenticate(bankUserRequest.getRoleId(), operatorUser, null, InitiatorActions.UPDATE);
            return bankUserService.setup(bankUserRequest, operatorUser, true);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to update", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Unable to update user.", e);
            return new ResponseEntity<>(new ErrorDetails("Failed to update user."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/toggle")
    public ResponseEntity toggleBankUser(@Valid @RequestBody Id id,
                                         @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) BankLoginDetails bankLoginDetails) {

        User operatorUser = userService.get(bankLoginDetails.getUserId());
        if (operatorUser == null) {
            return ErrorDetails.setUpErrors("Unable to toggle", Arrays.asList(Errors.UNKNOWN_USER.getValue()), "404");
        }
        try {
            return ResponseEntity.ok(bankUserService.toggle(id.getId(), operatorUser));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to toggle", Arrays.asList(e.getMessage()), e.getCode());
        } catch (Exception e) {
            log.error("Unable to toggle user.", e);
            return new ResponseEntity<>(new ErrorDetails("Failed to toggle user."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/authorization")
    public ResponseEntity approveAction(@Valid @RequestBody AuthorizationRequest request, @RequestParam AuthorizationAction action,
                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        BankUser bankUser = null;
        try {
            bankUser = bankUserService.performAuthorization(request, action, userDetail);
            return ResponseEntity.status(200).body(bankUser);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Authorization failed", Arrays.asList(e.getMessage()), e.getCode());
        }catch(Exception e){
            log.error("Authorization failed.",e);
            return new ResponseEntity<>(new ErrorDetails("Authorization failed."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status")
    public ResponseEntity getAllByStatus(@RequestParam boolean status) {
        try {
            return ResponseEntity.ok(bankUserService.getByStatus(status));
        }catch (Exception e){
            log.error("Failed to load activated users.",e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/search")
    public ResponseEntity searchBankUsers(@RequestBody UsersSearchRequest request, Pageable pageable) {
        return ResponseEntity.ok(bankUserService.doSearch(request,pageable));
    }

//    @PostMapping("/search")
//    public ResponseEntity searchBankUsers(@RequestBody UsersSearchRequest request, Pageable pageable) {
//        try {
//            return searchRepo.searchBankUser(request.getRole(), request.getEmail(), request.getFirstName(),
//                    request.getMiddleName(), request.getLastName(), request.getPhoneNumber(), request.getCity(),
//                    request.getStaffNumber(), request.getActivated(), request.getCreatedAt(), pageable);
//
//        } catch (Exception e) {
//            log.error("Unable to complete search operation.", e);
//            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

}
