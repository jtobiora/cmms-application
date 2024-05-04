package ng.upperlink.nibss.cmms.service.makerchecker;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.NibssUser;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.pssp.PsspUser;
import ng.upperlink.nibss.cmms.repo.makerchecker.AuthorizationRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import ng.upperlink.nibss.cmms.service.nibss.NibssUserService;
import ng.upperlink.nibss.cmms.service.pssp.PsspUserService;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service

public class UserAuthorizationService {

    @Value("${encryption.salt}")
    private String salt;
    @Value("${email_from}")
    private String fromEmail;

    private RoleService roleService;
    private NibssUserService nibssUserService;
    private BankService bankService;
    private UserService userService;
    private BankUserService bankUserService;
    private BillerService billerService;
    private BillerUserService billerUserService;
    private PsspUserService psspUserService;
    private AuthorizationRepo authorizationRepo;
    private SmtpMailSender smtpMailSender;

    public static AuthorizationStatus[] rejectionStatuses = {
            AuthorizationStatus.CREATION_REJECTED,
            AuthorizationStatus.UPDATE_REJECTED,
            AuthorizationStatus.TOGGLE_REJECTED
    };
    public static AuthorizationAction[] REJECT_ACTIONS = {
            AuthorizationAction.REJECT_TOGGLE,
            AuthorizationAction.REJECT_UPDATE,
            AuthorizationAction.REJECT_CREATE
    };
    public static AuthorizationAction[] APPROVE_ACTIONS = {
            AuthorizationAction.APPROVE_TOGGLE,
            AuthorizationAction.APPROVE_CREATE,
            AuthorizationAction.APPROVE_UPDATE
    };
    public static InitiatorActions[] INITIATE_ACTIONS = {
            InitiatorActions.CREATE,
            InitiatorActions.UPDATE,
            InitiatorActions.TOGGLE
    };

    public static AuthorizationStatus[] pendingActions = {
            AuthorizationStatus.UNAUTHORIZED_CREATE,
            AuthorizationStatus.UNAUTHORIZED_TOGGLE,
            AuthorizationStatus.UNAUTHORIZED_UPDATE
    };

    @Autowired
    public void setAuthorizationRepo(AuthorizationRepo authorizationRepo) {
        this.authorizationRepo = authorizationRepo;
    }

    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }

    @Autowired
    public void setPsspUserService(PsspUserService psspUserService) {
        this.psspUserService = psspUserService;
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
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setBankUserService(BankUserService bankUserService) {
        this.bankUserService = bankUserService;
    }

    @Autowired
    public void setBillerService(BillerService billerService) {
        this.billerService = billerService;
    }

    @Autowired
    public void setBillerUserService(BillerUserService billerUserService) {
        this.billerUserService = billerUserService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    private void saveAction(AuthorizationTable authorizationTable)
    { 
      authorizationRepo.save(authorizationTable);
    }

    public User initiateAction(String jsonData,User userToBeSaved, InitiatorActions action) throws CMMSException {
        switch (action)
        {
            case CREATE:
                userToBeSaved.setAuthorizationStatus(AuthorizationStatus.UNAUTHORIZED_CREATE);
                return userToBeSaved;
            case UPDATE:
                switch (userToBeSaved.getAuthorizationStatus())
                {
                    case UNAUTHORIZED_UPDATE:
                        userToBeSaved.setJsonData(jsonData);
                        return userToBeSaved;
                    case AUTHORIZED:
                        userToBeSaved.setJsonData(jsonData);
                        userToBeSaved.setAuthorizationStatus(AuthorizationStatus.UNAUTHORIZED_UPDATE);
                        return userToBeSaved;
                    case UNAUTHORIZED_CREATE:
                        return userToBeSaved;
                        default:
                            userToBeSaved.setJsonData(jsonData);
                            userToBeSaved.setAuthorizationStatus(AuthorizationStatus.UNAUTHORIZED_UPDATE);
                            return userToBeSaved;

                }
            case TOGGLE:
                switch (userToBeSaved.getAuthorizationStatus())
                {
                    case UNAUTHORIZED_CREATE:
                        throw new CMMSException(Errors.NO_ACTIONS_PERMITTED.getValue(),"400","400");
                    case UNAUTHORIZED_UPDATE:
                        throw new CMMSException(Errors.NO_ACTIONS_PERMITTED.getValue(),"400","400");
                    case UNAUTHORIZED_TOGGLE:
                        userToBeSaved.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);
                        break;
                        default:
                            userToBeSaved.setAuthorizationStatus(AuthorizationStatus.UNAUTHORIZED_TOGGLE);
                }
                return userToBeSaved;
                default:
                        {
                    throw new CMMSException(Errors.REQUEST_TERMINATED.getValue(),"400","400");
//                return null;
                         }
        }
    }
    public User rejectAction(User userToBeUpdated, AuthorizationAction action, AuthorizationRequest request) throws CMMSException {
        switch (action)
        {
            case REJECT_CREATE:
                if (!userToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.UNAUTHORIZED_CREATE))
                {
                    throw new CMMSException("Reject creation failed: This action is not permitted","401","401");
                }else
                {
                    userToBeUpdated.setAuthorizationStatus(AuthorizationStatus.CREATION_REJECTED);
                    userToBeUpdated.setReason(request.getReason());
                    return performSave(userToBeUpdated);
                }
            case REJECT_UPDATE:
                if (!userToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.UNAUTHORIZED_UPDATE))
                {
                    throw new CMMSException("Reject update failed: This action is not permitted","401","401");
                }else
                {
                    userToBeUpdated.setAuthorizationStatus(AuthorizationStatus.UPDATE_REJECTED);
                    userToBeUpdated.setReason(request.getReason());
                    return performSave(userToBeUpdated);
                }
            case REJECT_TOGGLE:
                if (!userToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.UNAUTHORIZED_TOGGLE))
                {
                    throw new CMMSException("Reject toggle failed: This action is not permitted","401","401");
                }else
                {
                    userToBeUpdated.setAuthorizationStatus(AuthorizationStatus.TOGGLE_REJECTED);
                    userToBeUpdated.setReason(request.getReason());
                    return performSave(userToBeUpdated);
                }

                default:
                {
                    throw new CMMSException(Errors.REQUEST_TERMINATED.getValue(),"400","400");
//                return null;
                }
        }
    }
    public User approveAction(User userToBeUpdated, AuthorizationAction action,User operator) throws CMMSException {
           switch (action) {
               case APPROVE_CREATE:
                   if (userToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.UNAUTHORIZED_CREATE)) {
                       userToBeUpdated.setActivated(true);
                       userToBeUpdated.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);

                       return performSave(userToBeUpdated);
                   } else {
                       throw new CMMSException("No pending data to be approved ", "400", "400");
                   }
               case APPROVE_UPDATE:
                   if (!userToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.UNAUTHORIZED_UPDATE) ||
                           StringUtils.isEmpty(userToBeUpdated.getJsonData())) {
                       throw new CMMSException("Failed: This action is not permitted ", "400", "400");
                   }
                   userToBeUpdated = getObject(userToBeUpdated, operator);
                   userToBeUpdated.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);
                   userToBeUpdated.setJsonData(null);
                   return performSave(userToBeUpdated);
               case APPROVE_TOGGLE:
                   userToBeUpdated.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);
                   userToBeUpdated = userService.toggle(userToBeUpdated.getId());
                   return performSave(userToBeUpdated);
               default: {
                   throw new CMMSException(Errors.REQUEST_TERMINATED.getValue(), "400", "400");
//                return null;
               }
           }
    }

    public User getObject(User userToBeUpdated, User operator) throws CMMSException {

            if (userToBeUpdated instanceof NibssUser) {
                return generateNibssUser(userToBeUpdated, operator);
            } else if (userToBeUpdated instanceof BankUser) {
                System.out.println("Inside: get object " + userToBeUpdated.getUserType());
                return generateBankUser(userToBeUpdated, operator);
            } else if (userToBeUpdated instanceof BillerUser) {
                return generateBillerUser(userToBeUpdated, operator);
            } else if (userToBeUpdated instanceof PsspUser) {
                return generatePsspUser(userToBeUpdated, operator);
            } else {
                throw new CMMSException("Unknown object : ", "400", "400");
            }
    }

    public User generatePsspUser(User userToBeUpdated, User operator) throws CMMSException {
       try {
           PsspUser psspUser = (PsspUser) userToBeUpdated;
           String jsonData = psspUser.getJsonData();
           ObjectMapper objectMapper = new ObjectMapper();
           PsspUser fronJson = objectMapper.readValue(jsonData, PsspUser.class);
           psspUser = psspUserService.generateApproved(psspUser, fronJson, operator);
           return psspUser;
       }catch (Exception e)
       {
           e.printStackTrace();
           log.error("Error track ----",e);
           throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");

       }
    }
    public User generateBillerUser(User userToBeUpdated, User operator) throws CMMSException {
        try {
            BillerUser billerUser = (BillerUser) userToBeUpdated;
            String jsonData = billerUser.getJsonData();
            ObjectMapper objectMapper = new ObjectMapper();
            BillerUser fronJson = objectMapper.readValue(jsonData, BillerUser.class);
            billerUser = billerUserService.generateApproved(billerUser, fronJson, operator);
            return billerUser;
        }catch (Exception e)
        {
            e.printStackTrace();
            log.error("Error track ----",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");

        }
    }

    public User generateBankUser(User userToBeUpdated, User operator) throws CMMSException {
        try {
            BankUser bankUser = (BankUser) userToBeUpdated;
            String jsonData = bankUser.getJsonData();
            ObjectMapper objectMapper = new ObjectMapper();
            BankUser fronJson = objectMapper.readValue(jsonData, BankUser.class);
            bankUser = bankUserService.generateApproved(bankUser, fronJson, operator);
            return bankUser;
        }catch (Exception e)
        {
            e.printStackTrace();
            log.error("Error track ----",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");

        }
    }

    public User generateNibssUser(User userToBeUpdated, User operator) throws CMMSException {
        try {
            NibssUser nibssUser = (NibssUser)userToBeUpdated;
            String jsonData = nibssUser.getJsonData();
            ObjectMapper objectMapper = new ObjectMapper();
            NibssUser fromJson = objectMapper.readValue(jsonData, NibssUser.class);
            nibssUser = nibssUserService.generateApproved(nibssUser,fromJson,  operator);
            return nibssUser;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error track ----",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");

        }
    }

    public User performSave(User userToBeUpdated) throws CMMSException {
        System.out.println();
        switch (userToBeUpdated.getUserType())
        {
            case NIBSS:
                return nibssUserService.save((NibssUser) userToBeUpdated);
            case BANK:
                return bankUserService.save((BankUser)userToBeUpdated);
            case BILLER:
                return billerUserService.save((BillerUser) userToBeUpdated);
            case PSSP:
                return psspUserService.save((PsspUser)userToBeUpdated);
            default:
            {
                throw new CMMSException("User type not found","400","400");
            }
        }
    }

    public User actions (String jsonData, User userToBeSaved, User operator, AuthorizationAction action, InitiatorActions initiatorActions,AuthorizationRequest request) throws CMMSException {

            switch (selectAction(initiatorActions,action,request))
            {
                case "INITIATE":
                    if (!operator.getRoles().stream().findAny().get().getUserAuthorisationType().equalsIgnoreCase("OPERATOR"))
                        throw new CMMSException("Your are not permitted to initiate action!","401","401");
                    userToBeSaved=initiateAction(jsonData,userToBeSaved,initiatorActions);
                    break;
                case "APPROVE":
                    if (!operator.getRoles().stream().findAny().get().getUserAuthorisationType().equalsIgnoreCase("AUTHORIZER"))
                        throw new CMMSException("Your are not permitted to authorize action!","401","401");
                    userToBeSaved= approveAction(userToBeSaved, action,operator);
                    break;
                case "REJECT":
                    if (!operator.getRoles().stream().findAny().get().getUserAuthorisationType().equalsIgnoreCase("AUTHORIZER"))
                        throw new CMMSException("Your are not permitted to reject!","401","401");
                    userToBeSaved = rejectAction(userToBeSaved,action,request);
            }
            if (userToBeSaved==null)
                throw new CMMSException(Errors.REQUEST_TERMINATED.getValue(),"401","401");
            else
            {
                return userToBeSaved;
//                return null;
            }
    }
    public String selectAction(InitiatorActions initiatorActions,AuthorizationAction authorizationAction,AuthorizationRequest request) throws CMMSException {
        if (Arrays.asList(INITIATE_ACTIONS).contains(initiatorActions))
            return "INITIATE";
        else if (Arrays.asList(APPROVE_ACTIONS).contains(authorizationAction))
            return "APPROVE";
        else if (Arrays.asList(REJECT_ACTIONS).contains(authorizationAction))
        {
            if (StringUtils.isEmpty(request.getReason()))
            {
                throw new CMMSException("FAILED: Provide reason for the rejection","400","400");
            }
            return "REJECT";
        }
        else {
            throw new CMMSException(" No action found, please specify the action you want to perform","400","400");
//            return null;
        }
    }
    public  User generateUser(User nibssJson, User userToBeUpdated, User operator){
        userToBeUpdated.setContactDetails(nibssJson.getContactDetails());
        userToBeUpdated.setEmailAddress(nibssJson.getEmailAddress());
        userToBeUpdated.setName(nibssJson.getName());
        userToBeUpdated.setRoles(nibssJson.getRoles());
        userToBeUpdated.setPhoneNumber(nibssJson.getPhoneNumber());
//        userToBeUpdated.setU(new User(operator.getId(),operator.getName(),operator.getEmailAddress()));
        userToBeUpdated.setJsonData(null);
        userToBeUpdated.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);
        return userToBeUpdated;
    }
    public User performAuthorization(AuthorizationRequest request, AuthorizationAction authorizationAction, UserDetail userDetail) throws CMMSException {
        User user;
        User operatorUser =null;
        //get the user logged in
        if (request.getId() !=null)
        {
            operatorUser = userService.get(userDetail.getUserId());
            if (operatorUser == null)
            {
                throw new CMMSException(Errors.UNKNOWN_USER.getValue()+" login and try again","404","404");
            }
        }

        if (authorizationAction == null){
            throw new CMMSException("Please specify the action","400","400");
        }
        request.setAuthorization(authorizationAction);
        //get the user to be updated
        user = userService.get(request.getId());
        if (user == null)
        {
            throw new CMMSException("This user doesn't exist.","404","404");
        }
//            Long userRoleId = user.getRoles().iterator().next().getId();
        Long userRoleId = user.getRoles().stream().map(role -> role.getId()).findAny().get();
        switch (user.getUserType())
        {
            case NIBSS:
                Role newUserRole = roleService.get(userRoleId);
                if (newUserRole == null)
                    throw new CMMSException("User role does not exist","404","404");
                nibssUserService.authenticate(newUserRole,operatorUser,authorizationAction,null);
                break;

            case BANK:
                bankUserService.authenticate(userRoleId,operatorUser,authorizationAction,null);
                break;
            case BILLER:
                billerUserService.authenticate(userRoleId,operatorUser,authorizationAction,null);
            break;
            case PSSP:
                psspUserService.authenticate(userRoleId,operatorUser,authorizationAction,null);
            break;
            default:
                throw new CMMSException("Authentication ,User type not found","400","400");
        }
        String password = null;
        if (authorizationAction.equals(AuthorizationAction.APPROVE_CREATE))
        {
            password = userService.generatePassword();
            if (!password.isEmpty())
                user.setPassword(EncyptionUtil.doSHA512Encryption(password, salt));
        }

        user = actions(null,user, operatorUser, authorizationAction,null,request);
//            user = save(bankUser);
        if (user == null)
        {
            throw new CMMSException("Failed: Could not authorize.","500","500");
        }
/*
* Send mail to the newly created user
* */

        if (password!= null && authorizationAction.equals(AuthorizationAction.APPROVE_CREATE))
        {
            try {
                sendAwarenessMail(user,password,false,fromEmail);
            }catch (Exception e)
            {
                log.error("Mail error {}",e);
            }
        }
        return user;
//            nibss = (NibssUser)nibssUserService.save(nibss);
    }
    public void sendAwarenessMail(User user, String password, boolean isPasswordChange, String fromEmail) {

        String emailAddress = user.getEmailAddress();
        String[] email = {emailAddress};

        if (email.length > 0) {
            String subject = "Login Credentials";
            String title = "Account Creation";
            String message = "Your account  has been Created on Central Mandate Management System(CMMS). Please Login to change your password";
            if (isPasswordChange) {
                message = "The Changes on your account have been updated.";
                subject = "Password reset";
                title = "Account Update";
            }

            //Send the mail
            smtpMailSender.sendMail(fromEmail, email, subject,
                    title, message, generateDetails(user, isPasswordChange, password));
        }

    }

    private String generateDetails(User user, boolean isPasswordUpdate, String password) {

        String details = "";
        if (isPasswordUpdate) {
            details += "<strong>Your password reset was successful :</strong> " + "<br/>";
            details += "<strong>Date :</strong> " + user.getUpdatedAt() + "<br/>";
        } else {
            details += "<strong>Username :</strong> " + user.getEmailAddress() + "<br/>";
            details += "<strong>Password :</strong> " + password + "<br/>";
        }
        return details;
    }

}