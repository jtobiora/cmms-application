package ng.upperlink.nibss.cmms.service.biller;


import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.BillerUserRequest;
import ng.upperlink.nibss.cmms.dto.search.UsersSearchRequest;
import ng.upperlink.nibss.cmms.embeddables.ContactDetails;
import ng.upperlink.nibss.cmms.embeddables.Name;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.enums.makerchecker.ViewAction;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.contact.Lga;
import ng.upperlink.nibss.cmms.model.contact.State;
import ng.upperlink.nibss.cmms.repo.biller.BillerRepo;
import ng.upperlink.nibss.cmms.repo.biller.BillerUserRepo;
import ng.upperlink.nibss.cmms.service.NotificationConfig;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.contact.LgaService;
import ng.upperlink.nibss.cmms.service.makerchecker.UserAuthorizationService;
import ng.upperlink.nibss.cmms.util.CommonUtils;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BillerUserService {

    private static Logger LOG = LoggerFactory.getLogger(BankUserService.class);

    private NotificationConfig notificationConfig;
    private BillerService billerService;
    private RoleService roleService;
    private BillerUserRepo billerUserRepo;
    private BillerRepo billerRepo;
    private LgaService lgaService;
    private BankUserService bankUserService;
    private UserService userService;
    private Biller biller = new Biller();
    private UserType userType = UserType.BILLER;
    private SmtpMailSender smtpMailSender;
    @Value("${encryption.salt}")
    private String salt;

    @Value("${email_from}")
    private String fromEmail;

    private UserAuthorizationService userAuthorizationService;

    @Autowired
    public void setUserAuthorizationService(UserAuthorizationService userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
    }

    @Autowired
    public void setBillerService(BillerService billerService) {
        this.billerService = billerService;
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
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }

    @Autowired
    public void setBillerUserRepo(BillerUserRepo billerUserRepo) {
        this.billerUserRepo = billerUserRepo;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setLgaService(LgaService lgaService) {
        this.lgaService = lgaService;
    }

    public Page<BillerUser> getAll(Pageable pageable) {
        return billerUserRepo.findAll(pageable);
    }

    public Page<BillerUser> getAllByBillerId(Long billerId, Pageable pageable) {
        return billerUserRepo.getAllByBillerId(billerId, AuthorizationStatus.CREATION_REJECTED, pageable);
    }

    public Page<BillerUser> getAllByActiveStatusAndBillerId(boolean activated, Long billerId, Pageable pageable) {
        return billerUserRepo.getAllByActiveStatusAndBillerId(activated, billerId, AuthorizationStatus.CREATION_REJECTED, pageable);
    }

    public BillerUser getById(Long id) {

        return billerUserRepo.getById(id);
    }

    public List<String> getAllActiveAuthorizerEmailAddress(Long billerId) {
        return billerUserRepo.getAllActiveAuthorizerEmailAddress(billerId);
    }

    public long getCountOfSameEmailAddress(String emailAddress, Long id) {
        if (id == null) {
            return userService.getCountOfUsersByEmailAddress(emailAddress);
        }
        return userService.getCountOfUsersByEmailAddressNotId(emailAddress, id);
    }

    public BillerUser save(BillerUser billerUser) {
        return billerUserRepo.save(billerUser);
    }

    public BillerUser getUserById(Long userId) {
        return billerUserRepo.getById(userId);
    }

    public void validate(BillerUserRequest request, boolean isUpdate, Long id) throws CMMSException {

        BillerUser billerUser = null;
        if (isUpdate) {
            if (id == null) {
                throw new CMMSException("BillerUser id is not provided", "401", "401");
            }
        }
        userService.validate(userService.getUserRequest(request), isUpdate, request.getUserId());
    }

    public BillerUser generate(BillerUser newBillerUser, BillerUser existingUser, BillerUserRequest billerUserRequest, User operator, boolean isUpdate, UserType userType) throws CMMSException {
        try {
            newBillerUser = generateUpdate(newBillerUser, billerUserRequest, operator);
            if (operator == null) {
                throw new CMMSException("Operator is null, please log in", "400", "400");
            }
            if (isUpdate) {
                newBillerUser.setUpdatedBy(userService.setUser(operator));
                String jsonData = JsonBuilder.generateJson(newBillerUser);
                return (BillerUser) userAuthorizationService.actions(jsonData, existingUser, operator, null, InitiatorActions.UPDATE, null);

            } else {
                newBillerUser.setChange_password(true);
                newBillerUser.setUserType(userType);
                newBillerUser.setStaffNumber(billerUserRequest.getStaffNumber());
                newBillerUser.setActivated(false);
                newBillerUser.setBiller(new Biller(billerUserRequest.getBiller().getId(), billerUserRequest.getBiller().getName(), billerUserRequest.getBiller().getRcNumber(), billerUserRequest.getBiller().getApiKey(), billerUserRequest.getBiller().getBillerOwner()));
                newBillerUser.setCreatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.getUserType()));

                return (BillerUser) userAuthorizationService.actions(null, newBillerUser, operator, null, InitiatorActions.CREATE, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Error track --- ", e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(), "500", "500");
        }
    }


    public ContactDetails getContactDetails(BillerUserRequest userReq, Lga lga) {
        ContactDetails contact = new ContactDetails();
        contact.setHouseNumber(Optional.ofNullable(userReq.getHouseNumber()).orElse(null));
        contact.setStreetName(Optional.ofNullable(userReq.getStreetName()).orElse(null));
        contact.setCity(Optional.ofNullable(userReq.getCity()).orElse(null));
        contact.setLga(Optional.ofNullable(lga).orElse(null));
        State state = lga.getState();
        contact.setState(Optional.ofNullable(state).orElse(null));
        contact.setCountry(Optional.ofNullable(state.getCountry()).orElse(null));
        return contact;
    }

    public BillerUser generateUpdate(BillerUser billerUser, BillerUserRequest userRequest, User operator) {
        billerUser.setContactDetails(userService.getContactDetails(userRequest));
        billerUser.setEmailAddress(userRequest.getEmailAddress());
        billerUser.setName(new Name(userRequest.getName()));
        billerUser.setPhoneNumber(userRequest.getPhoneNumber());
        Collection<Role> roles = this.roleService.getActivated(userRequest.getRoleId());
        billerUser.getRoles().addAll(roles);
        billerUser.setUpdatedBy(operator);
        return billerUser;
    }

    public BillerUser generateApproved(BillerUser billerUser, BillerUser fromJson, User operator) {
        billerUser.setContactDetails(fromJson.getContactDetails());
        billerUser.setEmailAddress(fromJson.getEmailAddress());
        billerUser.setName(fromJson.getName());
        billerUser.setPhoneNumber(fromJson.getPhoneNumber());
        // delete old roles and persist new
        Set<Role> oldRoles = billerUser.getRoles();
        if (oldRoles != null) {
            billerUser.getRoles().removeAll(billerUser.getRoles());
        }
        billerUser.getRoles().addAll(fromJson.getRoles());
        billerUser.setUpdatedAt(fromJson.getUpdatedAt());
        billerUser.setUpdatedBy(fromJson.getUpdatedBy());
        return billerUser;
    }

    public BillerUser toggle(Long userId, User operatorUser) throws CMMSException {
        if (operatorUser == null)
            return null;
        BillerUser billerUser = getById(userId);
        if (billerUser == null)
            return null;
        authenticate(billerUser.getRoles().iterator().next().getId(), operatorUser, null, InitiatorActions.TOGGLE);
        billerUser = (BillerUser) userAuthorizationService.actions(null, billerUser, operatorUser, null, InitiatorActions.TOGGLE, null);
        if (billerUser != null)
            return this.save(billerUser);
        else {
            throw new CMMSException("Could not toggleInit", "500", "500");
        }
        //        boolean activated = billerUser.isActivated();
//        billerUser.setActivated(!activated);
//        billerUser.setUpdatedBy(operatorUser);
//        billerUser = save(billerUser);
//
//        return billerUser;
    }

//    public void sendAwarenessEmail(BillerUser billerUser, String password, boolean isUpdated, String fromEmail) {
//
//
//        if (billerUser.getCreatedBy() == null) {
//            LOG.error("BANK User 'created by' is NULL");
//            return;
//        }
//
//        if (isUpdated && billerUser.getUpdatedBy() == null) {
//            LOG.error("BANK User 'updated by' is NULL");
//            return;
//        }
//        String emailAddress = billerUser.getEmailAddress();
//
//        String[] email = {emailAddress};
//
//        if (email.length > 0) {
//            String subject = "Account creation";
//            String title = "Login credential";
//            String message = "Your account  has been created on Central Mandate Management System(CMMS). Login in to change your password";
//            if (isUpdated) {
//                message = "Changes on your account has been updated";
//                title = "Password reset";
//                subject = "Account Update";
//            }
//
//            //Send the mail
//            smtpMailSender.sendMail(fromEmail, email, subject,
//                    title, message, generateDetails(billerUser, isUpdated, password));
//        }
//
//    }
//
//    private String generateDetails(BillerUser billerUser, boolean isUpdate, String password) {
//
//        String details = "";
//        if (isUpdate) {
//            details += "<strong>Your password reset successful : </strong> " + "<br/>";
//            details += "<strong>Date : </strong>  " + billerUser.getUpdatedAt() + "<br/>";
//        } else {
//            details += "<strong>Username :</strong> " + billerUser.getEmailAddress() + "<br/>";
//            details += "<strong>Password :</strong> " + password + "<br/>";
////            details += "<a href="+loginURL+"> <strong>Click here To Login and change password</strong></a><br/>";
//        }
//        return details;
//    }

    public ResponseEntity<?> setup(BillerUserRequest billerUserRequest, User operatorUser, boolean isUpdate) throws CMMSException {
        if (operatorUser == null) {
            throw new CMMSException(Errors.UNKNOWN_USER.getValue(), "404", "404");
        }

        validate(billerUserRequest, isUpdate, billerUserRequest.getUserId());

        BillerUser newBillerUser = new BillerUser();

        if (isUpdate) {
            BillerUser billerUser = getById(billerUserRequest.getUserId());
            if (billerUser == null) {
                throw new CMMSException("The user to be updated is null", "404", "404");
            }
            billerUser = generate(newBillerUser, billerUser, billerUserRequest, operatorUser, isUpdate, userType);
            billerUser = save(billerUser);
//            //send a mail to all the authorizers for awareness
//            sendAwarenessEmail(billerUser, null, isUpdate, fromEmail);
        } else {
            Biller biller = billerService.getBillerById(billerUserRequest.getBillerId());
            if (biller == null) {
                throw new CMMSException("Please specify the biller", "404", "404");
            }
            billerUserRequest.setBiller(biller);

//                BankUser unAuthorizedAdminCreation = bankUserService.getById(operatorUser.getId());
//                if (unAuthorizedAdminCreation !=null && unAuthorizedAdminCreation.getUserBank()!=null)
//                {
            if (biller.getBankAsBiller() != null) {
                throw new CMMSException("Can not create users for a Biller that is owned by bank. Bank Admins are the Biller Admin", "401", "401");
            }
//                }else
//                {
//                    return ResponseEntity.notFound().build();
//                }
//                if (operatorRole.getName().equals(RoleName.BANK_ADMIN))
//                {
//                        BankUser operatorBankUser = bankUserService.getById(operatorUser.getId());
//                        Set<Biller> ownedBillers = operatorBankUser.getUserBank().getOwnedBillers();
//                       if( ownedBillers.contains(biller))
//                            return ResponseEntity.badRequest().body("Users cannot be created for this biller because it is owned by your bank");
//
//                }

            newBillerUser = generate(newBillerUser, null, billerUserRequest, operatorUser, isUpdate, userType);
            String password = userService.generatePassword();
            if (!password.isEmpty())
                newBillerUser.setPassword(EncyptionUtil.doSHA512Encryption(password, salt));
            newBillerUser = save(newBillerUser);

//            //send a mail to all the authorizers for awareness
//            sendAwarenessEmail(newBillerUser, password, isUpdate, fromEmail);

        }
        return ResponseEntity.ok(newBillerUser);
    }

    public void authenticate(Long userRoleId, User operatorUser, AuthorizationAction action, InitiatorActions initiatorActions) throws CMMSException {
        if (operatorUser == null) {
            throw new CMMSException(Errors.UNKNOWN_USER.getValue(), "404", "404");
        }

        Role newUserRole = this.roleService.getById(userRoleId);
        Collection<Role> roles = operatorUser.getRoles();
        if (roles == null) {
            throw new CMMSException("Operator role is null ", "401", "401");
        }

        Role operatorRole = roles.stream().findAny().get();

        RoleName[] authorizerRoles = {RoleName.BANK_ADMIN_AUTHORIZER, RoleName.NIBSS_AUTHORIZER, RoleName.BILLER_ADMIN_AUTHORIZER, RoleName.PSSP_ADMIN_AUTHORIZER};
        RoleName[] userRoleNames = {RoleName.BILLER_AUDITOR, RoleName.BILLER_AUTHORIZER, RoleName.BILLER_INITIATOR};
        RoleName[] adminRoleNames = {RoleName.BILLER_ADMIN_AUTHORIZER, RoleName.BILLER_ADMIN_INITIATOR};
        RoleName[] initiatorRoles = {RoleName.BANK_ADMIN_INITIATOR, RoleName.NIBSS_INITIATOR, RoleName.BILLER_ADMIN_INITIATOR, RoleName.PSSP_ADMIN_INITIATOR};

        if (!Arrays.asList(userRoleNames).contains(newUserRole.getName()) &&
                !Arrays.asList(adminRoleNames).contains(newUserRole.getName())) {
            throw new CMMSException(Errors.NOT_PERMITTED_TO_ACT.getValue().replace("{}", newUserRole.getName().getValue()), "404", "404");
        }
        if (Arrays.asList(adminRoleNames).contains(newUserRole.getName())
                && Arrays.asList(UserAuthorizationService.INITIATE_ACTIONS).contains(initiatorActions)) {

            if (!Arrays.asList(initiatorRoles).contains(operatorRole.getName())) {
                throw new CMMSException("You can't act on this user: " + Errors.NOT_PERMITTED, "401", "401");
            }

        }
        if (Arrays.asList(userRoleNames).contains(newUserRole.getName()) && Arrays.asList(UserAuthorizationService.INITIATE_ACTIONS).contains(initiatorActions)) {
            if (!operatorRole.getName().equals(RoleName.BILLER_ADMIN_INITIATOR)) {
                throw new CMMSException(Errors.NOT_PERMITTED.getValue(), "401", "401");
            }

        }
        if (Arrays.asList(userRoleNames).contains(newUserRole.getName()) && (Arrays.asList(UserAuthorizationService.APPROVE_ACTIONS).contains(action) ||
                Arrays.asList(UserAuthorizationService.REJECT_ACTIONS).contains(action))) {
            if (!operatorRole.getName().equals(RoleName.BILLER_ADMIN_AUTHORIZER)) {
                throw new CMMSException(Errors.NOT_PERMITTED.getValue(), "401", "401");
            }

        }

        if (Arrays.asList(adminRoleNames).contains(newUserRole.getName()) && Arrays.asList(UserAuthorizationService.INITIATE_ACTIONS).contains(initiatorActions)) {
            if (operatorRole.getName().equals(RoleName.BILLER_ADMIN_INITIATOR)) {
                throw new CMMSException(Errors.NOT_PERMITTED.getValue(), "401", "401");
            }

        }
        if (Arrays.asList(adminRoleNames).contains(newUserRole.getName()) && (Arrays.asList(UserAuthorizationService.APPROVE_ACTIONS).contains(action) ||
                Arrays.asList(UserAuthorizationService.REJECT_ACTIONS).contains(action))) {
            if (operatorRole.getName().equals(RoleName.BILLER_ADMIN_AUTHORIZER)) {
                throw new CMMSException(Errors.NOT_PERMITTED.getValue(), "401", "401");
            }

        }

        if (Arrays.asList(UserAuthorizationService.APPROVE_ACTIONS).contains(action) || Arrays.asList(UserAuthorizationService.REJECT_ACTIONS).contains(action)) {
            if (!Arrays.asList(authorizerRoles).contains(operatorRole.getName())) {
                throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE.getValue().replace("{}", newUserRole.getName().getValue()), "401", "400");
            }
        }
    }

    public List<BillerUser> getUsersByUserType(UserType userType, boolean activated, RoleName roleName, Biller biller) {
        return billerUserRepo.getUsersByUserTypeAndRole(roleName, userType, activated, biller);
    }

    public Page<BillerUser> selectView(Long id, ViewAction viewAction, Pageable pageable) {
        switch (viewAction) {
            case UNAUTHORIZED:
                return this.getAllPendingUsers(id, pageable);
            case AUTHORIZED:
                return getAllApprovedUsers(id, AuthorizationStatus.AUTHORIZED, pageable);
            case REJECTED:
                return getAllRejectedUsers(id, Arrays.asList(UserAuthorizationService.rejectionStatuses), pageable);
            default:
                return getAllApprovedUsers(id, AuthorizationStatus.AUTHORIZED, pageable);
        }
    }

    private Page<BillerUser> getAllPendingUsers(Long id, Pageable pageable) {
        return billerUserRepo.getAllPendingUsers(id, Arrays.asList(UserAuthorizationService.pendingActions), pageable);
    }

    private Page<BillerUser> getAllApprovedUsers(Long id, AuthorizationStatus authStatus, Pageable pageable) {
        return billerUserRepo.getAllApprovedUsers(id, authStatus, pageable);
    }

    private Page<BillerUser> getAllRejectedUsers(Long id, List<AuthorizationStatus> authStatus, Pageable pageable) {
        return billerUserRepo.getAllRejectedUsers(id, authStatus, pageable);
    }

    public BillerUser previewUpdate(Long id) throws CMMSException {
        BillerUser fromJson = billerUserRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        if (fromJson == null) {
            throw new CMMSException(Errors.NO_UPDATE_REQUEST.getValue(), "404", "404");
        }
        String jsonData = fromJson.getJsonData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            BillerUser jsonUser = mapper.readValue(jsonData, BillerUser.class);
            return jsonUser;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Error track ----", e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(), "500", "500");
        }
    }

    public BillerUser performAuthorization(AuthorizationRequest request, AuthorizationAction action, UserDetail userDetail) throws CMMSException {
        BillerUser user;
        request.setAuthorization(action);

        user = (BillerUser) userAuthorizationService.performAuthorization(request, action, userDetail);
        user = save(user);
        return user;
//            nibss = (NibssUser)nibssUserService.save(nibss);
    }

    long countAllByBillerId(Long id) {
        return billerUserRepo.countAllByBillerId(id);
    }

    long countAllByBillerIdAndStatus(boolean status, Long id) {
        return billerUserRepo.countAllByBillerIdAndStatus(status, id);
    }

    long countAll(Long id) {
        return billerUserRepo.countAll(id);
    }

    long countAllByStatus(boolean status) {
        return billerUserRepo.countAllByStatus(status);
    }

    public Page<BillerUser> doSearch(UsersSearchRequest req, Pageable pageable) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            boolean statusVar;

            //status provided but date_created NOT provided
            if (!StringUtils.isEmpty(req.getActivated()) && StringUtils.isEmpty(req.getCreatedAt())) {
                statusVar = Boolean.parseBoolean(req.getActivated());
                return billerUserRepo.search(req.getEmail(), req.getRole(), statusVar, pageable);
            }

            //date_created provided but status NOT provided
            if (!StringUtils.isEmpty(req.getCreatedAt()) && StringUtils.isEmpty(req.getActivated())) {
                Date from = CommonUtils.startOfDay(dateFormat.parse(req.getCreatedAt()));
                Date to = CommonUtils.endOfDay(dateFormat.parse(req.getCreatedAt()));
                return billerUserRepo.search(req.getEmail(),req.getRole(),from,to,pageable);
            }

            //created date and status are provided
            if(!StringUtils.isEmpty(req.getCreatedAt()) && !StringUtils.isEmpty(req.getActivated())){
                statusVar = Boolean.parseBoolean(req.getActivated());
                Date from = CommonUtils.startOfDay(dateFormat.parse(req.getCreatedAt()));
                Date to = CommonUtils.endOfDay(dateFormat.parse(req.getCreatedAt()));
                return billerUserRepo.search(req.getEmail(),req.getRole(),from,to,statusVar,pageable);
            }

            return billerUserRepo.search(req.getEmail(),req.getRole(),pageable);


        } catch (ParseException ex) {
            LOG.error("Date format incorrect ",ex.getMessage());
        }catch(Exception e){
            LOG.error("Unknown error occured ",e);
        }

        return new PageImpl<>(new ArrayList<>(), pageable,0);
    }
}