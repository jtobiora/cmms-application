package ng.upperlink.nibss.cmms.controller;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.BillerLoginDetails;
import ng.upperlink.nibss.cmms.dto.biller.BillerUserRequest;
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
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.repo.SearchRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import ng.upperlink.nibss.cmms.service.makerchecker.UserAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;

/**
 * REST controller for managing BillerUser.
 */
@RestController
@RequestMapping("/user/biller")
@Slf4j
public class BillerUserController {
    //private static Logger LOG = LoggerFactory.getLogger(BillerUserController.class);

    private BillerUserService billerUserService;
    private BankUserService bankUserService;
    private BillerService billerService;
    private UserService userService;
    private UserAuthorizationService authorizationService;
    private SearchRepo searchRepo;

    @Value("${email_from}")
    private String fromEmail;

    @Value("${encryption.salt}")
    private String salt;

    private UserType userType = UserType.BILLER;

    private RoleService roleService;

    @Autowired
    public void setSearchRepo(SearchRepo searchRepo){
        this.searchRepo = searchRepo;
    }

    @Autowired
    public void setRoleService(RoleService roleService){
        this.roleService = roleService;
    }

    @Autowired
    public void setBankUserService(BankUserService bankUserService) {
        this.bankUserService = bankUserService;
    }
    @Autowired
    public void setBillerUserService(BillerUserService billerUserService) {
        this.billerUserService = billerUserService;
    }
    @Autowired
    public void setAuthorizationService(UserAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Autowired
    public void setBillerService(BillerService billerService)
    {
        this.billerService = billerService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity getAllUserByBillerId(@RequestParam Long biller, @RequestParam int pageNumber, @RequestParam int pageSize){
       try{
           return ResponseEntity.ok(billerUserService.getAllByBillerId(biller,new PageRequest(pageNumber,pageSize)));
       }catch(Exception e){
           log.error("Could not retrieve all biller users ",e);
           return new ResponseEntity<>(new ErrorDetails("Unable to load users."), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable Long id){
       try{
           return ResponseEntity.ok(billerUserService.getById(id));
       }catch(Exception e){
           log.error("Failed to load biller user with id {} ",id,e);
           return new ResponseEntity<>(new ErrorDetails("Unable to load user details."), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/previewUpdate")
    public ResponseEntity previewUpdate ( @RequestParam Long id,
                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail)
    {
        try {
            return ResponseEntity.status(201).body(billerUserService.previewUpdate(id));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to preview", Arrays.asList(e.getMessage()),e.getCode());
        }catch(Exception e){
            log.error("Preview failed",e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/viewAction")
    public ResponseEntity getAllPendingUsers(@RequestParam Long billerId,@RequestParam ViewAction viewAction, @RequestParam int pageNumber, @RequestParam int pageSize){
       try {
           return ResponseEntity.ok(billerUserService.selectView(billerId,viewAction,new PageRequest(pageNumber,pageSize)));
       }catch (Exception e){
           log.error("Operation failed ",e);
           return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/all")
    public ResponseEntity getAll(Pageable pageable){

        try {
            return ResponseEntity.ok(billerUserService.getAll(pageable));
        }catch(Exception e){
            log.error("Failed to load all users ",e);
            return new ResponseEntity<>(new ErrorDetails("Failed to load all users."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active/{status}{billerId}")
    public ResponseEntity getAllByActiveStatusAndBillerId(@RequestParam boolean status,@RequestParam Long billerId, Pageable pageable){
        try{
            return ResponseEntity.ok(billerUserService.getAllByActiveStatusAndBillerId(status,billerId, pageable));
        }catch(Exception e){
            log.error("Users with biller id {} could not be loaded ",billerId,e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping({"/active/{pageNumber}/{pageSize}"})
    public ResponseEntity getAllByStatus(@RequestParam Long billerId,@RequestParam boolean status, @RequestParam(required = false) Integer pageNumber, @RequestParam(required = false) Integer pageSize) {
           try {
               return ResponseEntity.ok(billerUserService.getAllByActiveStatusAndBillerId(status, billerId,new PageRequest(pageNumber, pageSize)));
           }catch(Exception e){
               log.error("Users could not be loaded. ",e);
               return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
           }
    }

//    @GetMapping("/active")
//    public ResponseEntity getAllByStatus(@RequestParam boolean status) {
//
//        return ResponseEntity.ok(billerUserService.getAllByActiveStatus(status));
//    }

    @PostMapping
    public ResponseEntity<?> createBillerUser(@RequestBody BillerUserRequest billerUserRequest,
                                             @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) throws IOException {

        User operatorUser = userService.get(userDetail.getUserId());
        if (operatorUser == null)
        {
            return ErrorDetails.setUpErrors("Unable to create", Arrays.asList(Errors.UNKNOWN_USER.getValue()),"404");
        }
        try {
            billerUserService.authenticate(billerUserRequest.getRoleId(),operatorUser,null, InitiatorActions.CREATE);
            return billerUserService.setup(billerUserRequest,operatorUser,false);
        } catch (CMMSException e) {
            log.error("Unable to create user",e);
            return ErrorDetails.setUpErrors("Unable to create", Arrays.asList(e.getMessage()),e.getCode());
        }catch(Exception e){
            log.error("Unable to create user",e);
            return new ResponseEntity<>(new ErrorDetails("Unable to create user. Please try again."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping
    public ResponseEntity updateBillerUser(@RequestBody BillerUserRequest billerUserRequest,
                                      @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) BillerLoginDetails billerLoginDetails) throws IOException {
        User operatorUser = userService.get(billerLoginDetails.getUserId());
        if (operatorUser == null)
        {
            return ErrorDetails.setUpErrors("Unable to update", Arrays.asList(Errors.UNKNOWN_USER.getValue()),"404");
        }

        try {
            billerUserService.authenticate(billerUserRequest.getRoleId(),operatorUser,null,InitiatorActions.UPDATE);
            return billerUserService.setup(billerUserRequest,operatorUser,true);
        } catch (CMMSException e) {
            log.error("Unable to update user",e);
            return ErrorDetails.setUpErrors("Unable to update", Arrays.asList(e.getMessage()),e.getCode());
        }catch(Exception e){
            log.error("Updates failed ",e);
            return new ResponseEntity<>(new ErrorDetails("Unable to update user details. Please try again."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @PutMapping("/toggle")
    public ResponseEntity toggleBillerUser(@Valid @RequestBody Id id,
                                         @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) BillerLoginDetails billerLoginDetails){
        User operatorUser = userService.get(billerLoginDetails.getUserId());
        if (operatorUser == null){
            return ErrorDetails.setUpErrors("Unable to toggle", Arrays.asList(Errors.UNKNOWN_USER.getValue()),"404");
        }

        try {
            return ResponseEntity.ok(billerUserService.toggle(id.getId(),operatorUser));
        } catch (CMMSException e) {
            log.error("Unable to toggle user",e);
            return ErrorDetails.setUpErrors("Unable to toggle", Arrays.asList(e.getMessage()),e.getCode());
        }catch(Exception e){
            log.error("Unable to toggle user information",e);
            return new ResponseEntity<>(new ErrorDetails("Unable to perform operation. Please try again."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/authorization")
    public ResponseEntity approveAction(@Valid @RequestBody AuthorizationRequest request, @RequestParam AuthorizationAction action,
                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        BillerUser billerUser = null;
        try {
            billerUser = billerUserService.performAuthorization(request, action, userDetail);
            return ResponseEntity.status(200).body(billerUser);
        } catch (CMMSException e) {
            log.error("Authorization failed",e);
            return ErrorDetails.setUpErrors("Authorization failed:", Arrays.asList(e.getMessage()),e.getCode());
        }catch(Exception e){
            log.error("Authorization failed ",e);
            return new ResponseEntity<>(new ErrorDetails("Authorization failed. Please try again."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/search")
    public ResponseEntity searchBillerUsers(@RequestBody UsersSearchRequest request,
                                            Pageable pageable) {

        return ResponseEntity.ok(billerUserService.doSearch(request,pageable));
    }

}