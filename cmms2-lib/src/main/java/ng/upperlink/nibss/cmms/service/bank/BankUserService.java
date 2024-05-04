package ng.upperlink.nibss.cmms.service.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.bank.BankUserRequest;
import ng.upperlink.nibss.cmms.dto.search.UsersSearchRequest;
import ng.upperlink.nibss.cmms.embeddables.Name;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.MakerCheckerType;
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
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.repo.bank.BankRepo;
import ng.upperlink.nibss.cmms.repo.bank.BankUserRepo;
import ng.upperlink.nibss.cmms.service.NotificationConfig;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
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
import java.util.stream.Collectors;

@Service
public class BankUserService {
    private NotificationConfig notificationConfig;
    private static Logger LOG = LoggerFactory.getLogger(BankUserService.class);
    private RoleService roleService;
    private BankUserRepo bankUserRepo;
    private BankRepo bankRepo;
    private LgaService lgaService;
    private UserService userService;
    private BankService bankService;
    private Bank userBank = new Bank();
    private UserType userType = UserType.BANK;
    private SmtpMailSender smtpMailSender;
    @Value("${encryption.salt}")
    private String salt;
    @Value("${email_from}")
    private String fromEmail;

    //    AuthorizationStatus[] pendingActions = {AuthorizationStatus.UNAUTHORIZED_CREATE,
//            AuthorizationStatus.UNAUTHORIZED_TOGGLE, AuthorizationStatus.UNAUTHORIZED_UPDATE};
    private UserAuthorizationService userAuthorizationService;

    @Autowired
    public void setUserAuthorizationService(UserAuthorizationService userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
    }

    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }

    @Autowired
    public void setBankUserRepo(BankUserRepo bankUserRepo) {
        this.bankUserRepo = bankUserRepo;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setLgaService(LgaService lgaService) {
        this.lgaService = lgaService;
    }

    public Page<BankUser> getAll(Pageable pageable) {
        return bankUserRepo.findAll(pageable);
    }

    public Page<BankUser> getAllByBankId(Long bankId, Pageable pageable) {
        return bankUserRepo.getAllByBankId(bankId, AuthorizationStatus.CREATION_REJECTED, pageable);
    }

    public Page<BankUser> getAllByStatusAndBankId(boolean activated, Long bankId, Pageable pageable) {
        return bankUserRepo.getAllByStatusAndBankId(activated, bankId,AuthorizationStatus.CREATION_REJECTED, pageable);
    }

    public BankUser getById(Long id) {
        return bankUserRepo.getById(id);
    }

    public List<String> getAllActiveAuthorizerEmailAddress(Long bankId) {
        return bankUserRepo.getAllActiveAuthorizerEmailAddress(bankId,AuthorizationStatus.CREATION_REJECTED);
    }

    public long getCountOfSameEmailAddress(String emailAddress, Long id) {
        if (id == null) {
            return userService.getCountOfUsersByEmailAddress(emailAddress);
        }
        return userService.getCountOfUsersByEmailAddressNotId(emailAddress, id);
    }

    public Page<BankUser> getAllByActiveStatus(Long id,boolean activated, Pageable pageable) {
        return bankUserRepo.getAllByActiveStatus(id,activated,AuthorizationStatus.CREATION_REJECTED, pageable);
    }

    public List<BankUser> getAllByActiveStatus(Long id,boolean activated) {
        return bankUserRepo.getAllByActiveStatus(id,activated, AuthorizationStatus.CREATION_REJECTED);
    }

    public BankUser save(BankUser bankUser) {

        return bankUserRepo.save(bankUser);
    }

    public Bank getUserBank(String bankCode) {
        return bankRepo.getBankByCode(bankCode);
    }

    public void validate(BankUserRequest request, boolean isUpdate, Long id) throws CMMSException {

        if (isUpdate) {
            if (id == null)

            {
                throw new CMMSException("BankUser id is not provided\"", "404", "404");
            }
        }

        userService.validate(userService.getUserRequest(request), isUpdate, request.getUserId());
    }

    public BankUser toggle(Long userId, User operatorUser) throws CMMSException {

        BankUser bankUser = getById(userId);
        if (bankUser == null) {
            throw new CMMSException("No user found", "404", "404");
        }
        authenticate(bankUser.getRoles().iterator().next().getId(), operatorUser, null, InitiatorActions.TOGGLE);
        bankUser = (BankUser) userAuthorizationService.actions(null, bankUser, operatorUser, null, InitiatorActions.TOGGLE, null);

        if (bankUser != null)
            return this.save(bankUser);
        else {
            throw new CMMSException("Could not toggleInit", "500", "500");
        }
    }

    public BankUser generate(BankUser newBankUser, BankUser existingUser, BankUserRequest bankUserRequest, User operator, boolean isUpdate, UserType userType) throws CMMSException {

        newBankUser = generateUpdate(newBankUser, bankUserRequest);
        if (isUpdate) {
//            newBankUser.setUpdatedAt(new Date());
            newBankUser.setUpdatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.getUserType()));
            String jsonData = null;
            try {
                jsonData = JsonBuilder.generateJson(newBankUser);
                return (BankUser) userAuthorizationService.actions(jsonData, existingUser, operator, null, InitiatorActions.UPDATE, null);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                LOG.error("IOException----",e);
                throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(), "500", "500");
            }

        } else {

            newBankUser.setChange_password(true);
            newBankUser.setUserType(userType);
            newBankUser.setStaffNumber(bankUserRequest.getStaffNumber());
            newBankUser.setActivated(false);
            newBankUser.setUserBank(new Bank(bankUserRequest.getBank().getId(),
                    bankUserRequest.getBank().getCode(),
                    bankUserRequest.getBank().getName(), bankUserRequest.getBank().getApiKey()));
            newBankUser.setCreatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.getUserType()));

            return (BankUser) userAuthorizationService.actions(null, newBankUser, operator, null, InitiatorActions.CREATE, null);
        }

    }

    public BankUser generateUpdate(BankUser newBankUser, BankUserRequest userRequest) {
        newBankUser.setContactDetails(userService.getContactDetails(userRequest));
        newBankUser.setEmailAddress(userRequest.getEmailAddress());
        newBankUser.setName(new Name(userRequest.getName()));
        newBankUser.setPhoneNumber(userRequest.getPhoneNumber());
        Collection<Role> roles = this.roleService.getActivated(userRequest.getRoleId());

        String userAuthType = roles.stream().map(Role::getUserAuthorisationType).collect(Collectors.toList()).get(0);

        //added modifications
        newBankUser.setMakerCheckerType(MakerCheckerType.find(userAuthType));

        newBankUser.getRoles().addAll(roles);
        return newBankUser;
    }

    public BankUser generateApproved(BankUser bankUser, BankUser froJson, User operator) {
        bankUser.setContactDetails(froJson.getContactDetails());
        bankUser.setEmailAddress(froJson.getEmailAddress());
        bankUser.setName(froJson.getName());
        bankUser.setPhoneNumber(froJson.getPhoneNumber());
        bankUser.setMakerCheckerType(froJson.getMakerCheckerType());
        bankUser.setUpdatedBy(froJson.getUpdatedBy());
        bankUser.getRoles().addAll(froJson.getRoles());
        bankUser.setUpdatedAt(froJson.getUpdatedAt());
        return bankUser;
    }

//    public void sendAwarenessMail(BankUser bankUser, String password, boolean isUpdated, String fromEmail) {
//
//
//        if (bankUser.getCreatedBy() == null) {
//            LOG.error("BANK User 'created by' is NULL");
//            return;
//        }
//
//        if (isUpdated && bankUser.getUpdatedBy() == null) {
//            LOG.error("BANK User 'updated by' is NULL");
//            return;
//        }
//
//        String emailAddress = bankUser.getEmailAddress();
//        if (isUpdated) {
//            emailAddress = bankUser.getEmailAddress();
//        }
//
//        String[] email = {emailAddress};
//
//        if (email.length > 0) {
//            String subject = "Login Credentials";
//            String title = "Account Creation";
//            String message = "Your account  has been Created on Central Mandate Management System(CMMS), Login to change your password";
//            if (isUpdated) {
//                message = "Changes on your account has been updated";
//                subject = "Password reset";
//                title = "Account Update";
//            }
//
//            //Send the mail
//            smtpMailSender.sendMail(fromEmail, email, subject,
//                    title, message, generateDetails(bankUser, isUpdated, password));
//        }
//
//    }

//    private String generateDetails(BankUser bankUser, boolean isUpdate, String password) {
//
//        String details = "";
//        if (isUpdate) {
//            details += "<strong>Your password reset successful :</strong> " + "<br/>";
//            details += "<strong>Date :</strong> " + bankUser.getUpdatedAt() + "<br/>";
//        } else {
//            details += "<strong>Username :</strong> " + bankUser.getEmailAddress() + "<br/>";
//            details += "<strong>Password :</strong> " + password + "<br/>";
//        }
//        return details;
//    }

    public ResponseEntity<?> setup(BankUserRequest bankUserRequest, User operatorUser, boolean isUpdate) throws CMMSException {

        if (operatorUser == null) {
            throw new CMMSException("The bank operator not found", "404", "404");
        }

        validate(bankUserRequest, isUpdate, bankUserRequest.getUserId());
        BankUser setUpUser = new BankUser();

        if (isUpdate) {
            BankUser existingUser = getById(bankUserRequest.getUserId());
            if (existingUser == null) {
                throw new CMMSException("The bank user to be updated is null", "404", "404");
            }
            existingUser = generate(setUpUser, existingUser, bankUserRequest, operatorUser, isUpdate, userType);
            existingUser = save(existingUser);
//            //send a mail to all the authorizers for awareness
//            sendAwarenessMail(existingUser, null, isUpdate, fromEmail);
            return ResponseEntity.ok(existingUser);
        } else {
            Bank bank = bankService.getByBankId(bankUserRequest.getBankId());
            if (bank == null) {
                throw new CMMSException("Can not find the bank to tie the user", "404", "404");
            }

            bankUserRequest.setBank(new Bank(bank.getId(), bank.getCode(), bank.getName(), bank.getApiKey()));

            String password = userService.generatePassword();
            if (!password.isEmpty())
                setUpUser.setPassword(EncyptionUtil.doSHA512Encryption(password, salt));

            setUpUser = generate(setUpUser, null, bankUserRequest, operatorUser, isUpdate, userType);
            setUpUser = save(setUpUser);

//            //send a mail to all the authorizers for awareness
//            sendAwarenessMail(setUpUser, password, isUpdate, fromEmail);

            return ResponseEntity.ok(setUpUser);
        }
    }

    public void authenticate(Long userRoleId, User operatorUser, AuthorizationAction action, InitiatorActions initiatorActions) throws CMMSException {
        if (operatorUser == null) {
            throw new CMMSException(Errors.UNKNOWN_USER.getValue(), "404", "404");
        }

        Role newUserRole = this.roleService.getById(userRoleId);
        Collection<Role> roles = operatorUser.getRoles();
        if (roles == null) {
            throw new CMMSException("Operator role is null ", "404", "404");
        }

        Role operatorRole = roles.stream().findAny().get();

        RoleName[] userRoleNames = {RoleName.BANK_AUDITOR, RoleName.BANK_AUTHORIZER, RoleName.BANK_INITIATOR, RoleName.BANK_BILLER_INITIATOR, RoleName.BANK_BILLER_AUTHORIZER};
        RoleName[] adminRoleNames = {RoleName.BANK_ADMIN_INITIATOR, RoleName.BANK_ADMIN_AUTHORIZER};
        RoleName[] authorizerRole = {RoleName.BANK_ADMIN_AUTHORIZER, RoleName.NIBSS_AUTHORIZER};
        if (!Arrays.asList(userRoleNames).contains(newUserRole.getName()) && !Arrays.asList(adminRoleNames).contains(newUserRole.getName()))

        {
            throw new CMMSException("Not permitted to act on user with Role: " + newUserRole.getName().getValue(), "401", "401");
        }

        if (Arrays.asList(adminRoleNames).contains(newUserRole.getName()) && Arrays.asList(UserAuthorizationService.INITIATE_ACTIONS).contains(initiatorActions)) {
            if (!operatorRole.getName().equals(RoleName.NIBSS_INITIATOR))

            {
                throw new CMMSException(Errors.NOT_PERMITTED.getValue() + ": Only Initiator can perform this action", "401", "401");

            }
        }
        if (Arrays.asList(userRoleNames).contains(newUserRole.getName())) {
            if (!operatorRole.getName().equals(RoleName.BANK_ADMIN_INITIATOR)) {
                throw new CMMSException(Errors.NOT_PERMITTED.getValue() + " Only bank admin initiator can perform this action", "401", "401");
            }

        }

        if (Arrays.asList(UserAuthorizationService.APPROVE_ACTIONS).contains(action) || Arrays.asList(UserAuthorizationService.REJECT_ACTIONS).contains(action)) {
            if (!Arrays.asList(authorizerRole).contains(operatorRole.getName())) {
                throw new CMMSException("You can't authorize:  Not permitted", "401", "400");
            }
        }
    }


    public List<BankUser> getUsersByUserType(UserType userType, boolean activated, RoleName roleName, Bank bank) {
        return bankUserRepo.getUsersByUserTypeAndRole(roleName, userType, activated, bank);
    }

    public Page<BankUser> selectView(Long id,ViewAction viewAction, Pageable pageable) {
        switch (viewAction) {
            case UNAUTHORIZED:
                return this.getAllPendingUsers(id,pageable);
            case AUTHORIZED:
                return getAllApprovedUsers(id,AuthorizationStatus.AUTHORIZED, pageable);
            case REJECTED:
                return getAllRejectedUsers(id,Arrays.asList(UserAuthorizationService.rejectionStatuses), pageable);
            default:
                return getAllApprovedUsers(id,AuthorizationStatus.AUTHORIZED, pageable);
        }
    }

    private Page<BankUser> getAllPendingUsers(Long id,Pageable pageable) {
        return bankUserRepo.getAllPendingUsers(id, Arrays.asList(UserAuthorizationService.pendingActions), pageable);
    }

    private Page<BankUser> getAllApprovedUsers(Long id,AuthorizationStatus authStatus, Pageable pageable) {
        return bankUserRepo.getAllApprovedUsers(id,authStatus, pageable);
    }

    private Page<BankUser> getAllRejectedUsers(Long id,List<AuthorizationStatus> rejectedStatus, Pageable pageable) {
        return bankUserRepo.getAllRejectedUsers(id,rejectedStatus, pageable);
    }

    public BankUser previewUpdate(Long id) throws CMMSException {
        BankUser fromJson = bankUserRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        if (fromJson == null) {
            throw new CMMSException(Errors.NO_UPDATE_REQUEST.getValue(), "404", "404");
        }
        String jsonData = fromJson.getJsonData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            BankUser jsonUser = mapper.readValue(jsonData, BankUser.class);
            return jsonUser;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("IOException---",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(), "500", "500");
        }
    }

    public BankUser performAuthorization(AuthorizationRequest request, AuthorizationAction action, UserDetail userDetail) throws CMMSException {
        BankUser user;
        request.setAuthorization(action);
        user = (BankUser) userAuthorizationService.performAuthorization(request, action, userDetail);
//            user = save(user);
        return user;
//            nibss = (NibssUser)nibssUserService.save(nibss);
    }

    public Long getAll() {
        return bankUserRepo.getAllBankUsers().stream().count();
    }

    public Long getByStatus(boolean activeStatus) {
        return bankUserRepo.getByStatus(activeStatus).stream().count();
    }

    public Page<BankUser> doSearch(UsersSearchRequest req, Pageable pageable) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            boolean statusVar;

            //status provided but date_created NOT provided
            if (!StringUtils.isEmpty(req.getActivated()) && StringUtils.isEmpty(req.getCreatedAt())) {
                statusVar = Boolean.parseBoolean(req.getActivated());
                return bankUserRepo.search(req.getEmail(), req.getRole(), statusVar, pageable);
            }

            //date_created provided but status NOT provided
            if (!StringUtils.isEmpty(req.getCreatedAt()) && StringUtils.isEmpty(req.getActivated())) {
                Date from = CommonUtils.startOfDay(dateFormat.parse(req.getCreatedAt()));
                Date to = CommonUtils.endOfDay(dateFormat.parse(req.getCreatedAt()));
                return bankUserRepo.search(req.getEmail(),req.getRole(),from,to,pageable);
            }

            //created date and status are provided
            if(!StringUtils.isEmpty(req.getCreatedAt()) && !StringUtils.isEmpty(req.getActivated())){
                statusVar = Boolean.parseBoolean(req.getActivated());
                Date from = CommonUtils.startOfDay(dateFormat.parse(req.getCreatedAt()));
                Date to = CommonUtils.endOfDay(dateFormat.parse(req.getCreatedAt()));
                return bankUserRepo.search(req.getEmail(),req.getRole(),from,to,statusVar,pageable);
            }

            return bankUserRepo.search(req.getEmail(),req.getRole(),pageable);


        } catch (ParseException ex) {
            LOG.error("Date format incorrect ",ex.getMessage());
        }catch(Exception e){
            LOG.error("Unknown error occured ",e);
        }

        return new PageImpl<>(new ArrayList<>(), pageable,0);

    }
}
