package ng.upperlink.nibss.cmms.service.pssp;

import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.pssp.PsspUserRequest;
import ng.upperlink.nibss.cmms.dto.search.UsersSearchRequest;
import ng.upperlink.nibss.cmms.embeddables.Name;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.enums.makerchecker.ViewAction;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.pssp.Pssp;
import ng.upperlink.nibss.cmms.model.pssp.PsspUser;
import ng.upperlink.nibss.cmms.repo.bank.BankRepo;
import ng.upperlink.nibss.cmms.repo.pssp.PsspUserRepo;
import ng.upperlink.nibss.cmms.service.NotificationConfig;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class PsspUserService {
    private NotificationConfig notificationConfig;
    private static Logger LOG = LoggerFactory.getLogger(PsspUserService.class);
    private RoleService roleService;
    private PsspUserRepo psspUserRepo;
    private PsspService psspService;
    private BankRepo bankRepo;
    private LgaService lgaService;
    private UserService userService;
    private BankService bankService;
    private SmtpMailSender smtpMailSender;
    private UserAuthorizationService userAuthorizationService;
    @Value("${encryption.salt}")
    private String salt;
    @Value("${email_from}")
    private String fromEmail;

    private static final UserType userType = UserType.PSSP;

    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setUserAuthorizationService(UserAuthorizationService userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
    }

    @Autowired
    public void setPsspService(PsspService psspService)
    {
        this.psspService = psspService;
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
    public void setPsspUserRepo(PsspUserRepo psspUserRepo) {
        this.psspUserRepo = psspUserRepo;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setLgaService(LgaService lgaService) {
        this.lgaService = lgaService;
    }

    public Page<PsspUser> getAll(Pageable pageable) {
        return psspUserRepo.findAll(pageable);
    }

//    public Page<PsspUser> getAllByBankId(Long bankId, Pageable pageable) {
//        return psspUserRepo.getAllByBankId(bankId, pageable);
//    }
//
//    public Page<PsspUser> getAllByStatusAndBankId(boolean activated, Long bankId, Pageable pageable) {
//        return psspUserRepo.getAllByStatusAndBankId(activated, bankId, pageable);
//    }

    public PsspUser getById(Long id) {
        return psspUserRepo.getById(id);
    }

    public long getCountOfSameEmailAddress(String emailAddress, Long id) {
        if (id == null) {
            return userService.getCountOfUsersByEmailAddress(emailAddress);
        }
        return userService.getCountOfUsersByEmailAddressNotId(emailAddress, id);
    }

    public Page<PsspUser> getAllByActiveStatus(boolean activated, Pageable pageable) {
        return psspUserRepo.getAllByActiveStatus(activated, pageable);
    }

    public List<PsspUser> getAllByActiveStatus(boolean activated) {
        return psspUserRepo.getAllByActiveStatus(activated);
    }

    public PsspUser save(PsspUser psspUser) {
        return psspUserRepo.save(psspUser);
    }

    public Bank getUserBank(String bankCode) {
        return bankRepo.getBankByCode(bankCode);
    }

    public void validate(PsspUserRequest request, boolean isUpdate, Long id) throws CMMSException {

        PsspUser psspUser = null;
        if (isUpdate) {
            if (id == null) {
                if (id == null) { throw new CMMSException("PSSPUser id is not provided","401","401"); }
            }
        }

         userService.validate(userService.getUserRequest(request), isUpdate, request.getUserId());
    }

    public ResponseEntity toggleInit(Long userId, User operatorUser,AuthorizationAction action,InitiatorActions initiatorActions) throws  CMMSException {
        if (operatorUser == null) {
            return null;
        }
        PsspUser psspUser = getById(userId);

        if (psspUser == null) {
            return null;
        }

       authenticate(psspUser.getRoles().iterator().next().getId(), operatorUser,action,initiatorActions);


        psspUser = (PsspUser)userAuthorizationService.actions(null,psspUser,operatorUser,action,initiatorActions,null);

        return ResponseEntity.ok(psspUser);
    }

    public PsspUser toggleApprove(User operatorUser, PsspUser psspUser) {
        boolean activated = psspUser.isActivated();
        psspUser.setActivated(!activated);
        psspUser.setUpdatedBy(operatorUser);
        psspUser = save(psspUser);
        return psspUser;
    }

    public PsspUser generate(PsspUser newPsspUser,PsspUser existingUser, PsspUserRequest userReq, User operator, boolean isUpdate, UserType userType) throws  CMMSException {
        try {
            generateUpdate(newPsspUser, userReq);

            if (isUpdate) {
//            newPsspUser.setUpdatedAt(new Date());
                newPsspUser.setUpdatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.isActivated(), operator.getUserType()));
                String jsonData = JsonBuilder.generateJson(newPsspUser);
                return (PsspUser) userAuthorizationService.actions(jsonData, existingUser, operator, null, InitiatorActions.UPDATE, null);
            } else {
                newPsspUser.setChange_password(true);
                newPsspUser.setUserType(userType);
                newPsspUser.setStaffNumber(userReq.getStaffNumber());
                newPsspUser.setActivated(false);
                newPsspUser.setPssp(userReq.getPssp());
                newPsspUser.setCreatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.isActivated(), operator.getUserType()));
                return (PsspUser) userAuthorizationService.actions(null, newPsspUser, operator, null, InitiatorActions.CREATE, null);
            }
        } catch (Exception e ) {
            e.printStackTrace();
            LOG.error("Error track ---- ",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
        }
    }

    public void generateUpdate(PsspUser psspUser, PsspUserRequest userReq) {
        psspUser.setContactDetails(userService.getContactDetails(userReq));
        psspUser.setEmailAddress(userReq.getEmailAddress());
        psspUser.setName(new Name(userReq.getName()));
        psspUser.setPhoneNumber(userReq.getPhoneNumber());
        Collection<Role> roles = this.roleService.getActivated(userReq.getRoleId());

        psspUser.getRoles().addAll(roles);
    }

//    public ContactDetails getContactDetails(PsspUserRequest userReq, Lga lga) {
//        ContactDetails contact = new ContactDetails();
//        contact.setHouseNumber(Optional.ofNullable(userReq.getHouseNumber()).orElse(null));
//        contact.setStreetName(Optional.ofNullable(userReq.getStreetName()).orElse(null));
//        contact.setCity(Optional.ofNullable(userReq.getCity()).orElse(null));
//        contact.setLga(Optional.ofNullable(lga).orElse(null));
//        State state = lga.getState();
//        contact.setState(Optional.ofNullable(state).orElse(null));
//        contact.setCountry(Optional.ofNullable(state.getCountry()).orElse(null));
//        return contact;
//    }

    public PsspUser generateApproved(PsspUser psspUser, PsspUser fromJson, User operator) {
       psspUser.setContactDetails(fromJson.getContactDetails());

        psspUser.setEmailAddress(fromJson.getEmailAddress());
        psspUser.setName(fromJson.getName());
        psspUser.setPhoneNumber(fromJson.getPhoneNumber());
        psspUser.getRoles().addAll(fromJson.getRoles());
      // delete old roles and persist new
      Set<Role> oldRoles = psspUser.getRoles();
      if (oldRoles != null)
      {
          psspUser.getRoles().removeAll(psspUser.getRoles());
      }
      psspUser.getRoles().addAll(fromJson.getRoles());
      psspUser.setUpdatedAt(fromJson.getUpdatedAt());
      psspUser.setUpdatedBy(fromJson.getUpdatedBy());
      return psspUser;
    }

//    public void sendAwarenessMail(PsspUser psspUser, String password, boolean isUpdated, String fromEmail) {
//
//
//        if (psspUser.getCreatedBy() == null) {
//            LOG.error("PSSP User 'created by' is NULL");
//            return;
//        }
//
//        if (isUpdated && psspUser.getUpdatedBy() == null) {
//            LOG.error("PSSP User 'updated by' is NULL");
//            return;
//        }
//
//        String emailAddress = psspUser.getEmailAddress();
//        if (isUpdated) {
//            emailAddress = psspUser.getEmailAddress();
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
//                    title, message, generateDetails(psspUser, isUpdated, password));
//        }
//
//    }
//
//    private String generateDetails(PsspUser psspUser, boolean isUpdate, String password) {
//
//        String details = "";
//        if (isUpdate) {
//            details += "<strong>Your password reset successful :</strong> " + "<br/>";
//            details += "<strong>Date :</strong> " + psspUser.getUpdatedAt() + "<br/>";
//        } else {
//            details += "<strong>Username :</strong> " + psspUser.getEmailAddress() + "<br/>";
//            details += "<strong>Password :</strong> " + password + "<br/>";
//        }
//        return details;
//    }

    public ResponseEntity<?> setup(PsspUserRequest userReq,  User operatorUser ,AuthorizationAction action,InitiatorActions initiatorActions,boolean isUpdate) throws CMMSException {

        validate(userReq, isUpdate, userReq.getUserId());
        authenticate(userReq.getRoleId(),operatorUser,action,initiatorActions);

        PsspUser newPsspUser = new PsspUser();

        if (isUpdate) {
            PsspUser existingPsspUser = getById(userReq.getUserId());
            if (existingPsspUser == null) {
                throw new CMMSException("The PSSP user to be updated is null","404","404");
            }
            existingPsspUser = generate(newPsspUser,existingPsspUser, userReq, operatorUser, isUpdate, userType);

            existingPsspUser = save(existingPsspUser);

//            //send a mail to all the authorizers for awareness
//            sendAwarenessMail(existingPsspUser,null,isUpdate,fromEmail);

        } else {
            Pssp pssp =psspService.getPSSPById(userReq.getPsspId());
            if (pssp == null) {
                return ResponseEntity.status(404).body(new ErrorDetails("Cannot find the pssp to tie the user"));
            }

            userReq.setPssp(new Pssp(pssp.getId(),pssp.getPsspName(),pssp.getPsspCode(),pssp.getApiKey(),pssp.getRcNumber()));

            newPsspUser = generate(newPsspUser,null, userReq, operatorUser, isUpdate, userType);

            String password = userService.generatePassword();

            if (!password.isEmpty()) {
                newPsspUser.setPassword(EncyptionUtil.doSHA512Encryption(password, salt));
            }

            newPsspUser = save(newPsspUser);

//            //send a mail to all the authorizers for awareness
//            sendAwarenessMail(newPsspUser,password,isUpdate,fromEmail);

        }
        return ResponseEntity.ok(newPsspUser);
    }


    public void authenticate(Long userRoleId, User operatorUser, AuthorizationAction action, InitiatorActions initiatorActions) throws CMMSException {

        Role newUserRole = this.roleService.getById(userRoleId);
        Collection<Role> roles = operatorUser.getRoles();
        if (roles == null)
        {
            throw new CMMSException(Errors.NOT_PERMITTED.getValue(),"401","401");
        }

        Role operatorRole = roles.stream().findAny().get();

        RoleName[] userRoleNames = {RoleName.PSSP_AUTHORIZER,RoleName.PSSP_INITIATOR,RoleName.PSSP_AUDITOR};
        RoleName[] adminRoleNames = {RoleName.PSSP_ADMIN_INITIATOR,RoleName.PSSP_ADMIN_AUTHORIZER};
        RoleName[] initiatorRoles = {RoleName.BANK_ADMIN_INITIATOR,RoleName.NIBSS_INITIATOR};
        RoleName[] authorizerRoles = {RoleName.BANK_ADMIN_AUTHORIZER,RoleName.NIBSS_AUTHORIZER};

        //Can only create PSSP_ADMIN,PSSP_INITIATOR & PSSP_AUTHORIZER for PSSP's
        if(!Arrays.asList(userRoleNames).contains(newUserRole.getName()) &&
                !Arrays.asList(adminRoleNames).contains(newUserRole.getName()))
        {
            throw new CMMSException("This role " + newUserRole.getName().getValue() + " cannot be assigned to a PSSP user","401","401");
        }

        //PSSP_ADMIN_INITIATOR and PSSP_ADMIN_AUTHORIZER can only be created by NIBSS_INITIATOR or BANK_ADMIN_INITIATOR

        if (Arrays.asList(adminRoleNames).contains(newUserRole.getName())&& Arrays.asList(UserAuthorizationService.INITIATE_ACTIONS).contains(initiatorActions))
        {
            if (!Arrays.asList(initiatorRoles).contains(operatorRole.getName()))
            {
                throw new CMMSException(Errors.NOT_PERMITTED_TO_CREATE.getValue().replace("{}","PSSP User"),"401","401");
            }

        }
        if (Arrays.asList(userRoleNames).contains(newUserRole.getName())&& Arrays.asList(UserAuthorizationService.INITIATE_ACTIONS).contains(initiatorActions))
        {
            if (!operatorRole.getName().equals(RoleName.PSSP_ADMIN_INITIATOR))
            {
                throw new CMMSException(Errors.NOT_PERMITTED_TO_CREATE.getValue().replace("{}",newUserRole.getName().getValue()),"401","401");
            }

        }

        if (Arrays.asList(adminRoleNames).contains(newUserRole.getName())&&(Arrays.asList(UserAuthorizationService.APPROVE_ACTIONS).contains(action)||
                Arrays.asList(UserAuthorizationService.REJECT_ACTIONS).contains(action))){
            if (!Arrays.asList(authorizerRoles).contains(operatorRole.getName()))
            {
                throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE.getValue().replace("{}",newUserRole.getName().getValue()),"401","401");
            }
        }
        if (Arrays.asList(userRoleNames).contains(newUserRole.getName())&&(Arrays.asList(UserAuthorizationService.APPROVE_ACTIONS).contains(action)||
                Arrays.asList(UserAuthorizationService.REJECT_ACTIONS).contains(action))){
            if (!operatorRole.getName().equals(RoleName.PSSP_ADMIN_AUTHORIZER))
            {
                throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE.getValue().replace("{}",newUserRole.getName().getValue()),"401","401");
            }
        }
    }

    public PsspUser performAuthorization(AuthorizationRequest request, AuthorizationAction action, UserDetail userDetail) throws CMMSException {
        PsspUser user;
        request.setAuthorization(action);

        user = (PsspUser) userAuthorizationService.performAuthorization(request,action,userDetail);
//            user = save(bankUser);
        return user;
//            nibss = (NibssUser)nibssUserService.save(nibss);
    }

    public Page<PsspUser> selectView(Long id,ViewAction viewAction, Pageable pageable)
    {
        switch (viewAction)
        {
            case UNAUTHORIZED:
                return this.getAllPendingUsers(id,pageable);
            case AUTHORIZED:
                return getAllApprovedUsers(id,AuthorizationStatus.AUTHORIZED,pageable);
            case REJECTED:
                return getAllRejectedUsers(id,Arrays.asList(UserAuthorizationService.rejectionStatuses),pageable);
            default:
                return getAllApprovedUsers(id,AuthorizationStatus.AUTHORIZED,pageable);
        }
    }
    private Page<PsspUser> getAllPendingUsers(Long id,Pageable pageable)
    {
        return psspUserRepo.getAllPending(id,Arrays.asList(UserAuthorizationService.pendingActions),pageable);
    }
    private Page<PsspUser> getAllApprovedUsers(Long id,AuthorizationStatus authStatus, Pageable pageable)
    {
        return psspUserRepo.getAllApproved(id,authStatus,pageable);
    }
    private Page<PsspUser> getAllRejectedUsers(Long id,List<AuthorizationStatus> authStatus , Pageable pageable)
    {
        return psspUserRepo.getAllRejected(id,authStatus,pageable);
    }
    public PsspUser previewUpdate(Long id) throws CMMSException {
        PsspUser fromJson = psspUserRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        if (fromJson == null)
        {
            throw new CMMSException(Errors.NO_UPDATE_REQUEST.getValue(),"404","404");
        }
        String jsonData = fromJson.getJsonData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            PsspUser jsonUser = mapper.readValue(jsonData, PsspUser.class);
            return jsonUser;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Error track -----",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
        }
    }

    public Long getAll(){
        return psspUserRepo.getAllPSSPUsers().stream().count();
    }


    long countAllByBillerId(Long id)
    {
        return psspUserRepo.countAllByBillerId(id);
    }

    long countAllByBillerIdAndStatus(boolean status,Long id)
    {
        return psspUserRepo.countAllByBillerIdAndStatus(status,id);
    }

    long countAll(Long id)
    {
        return psspUserRepo.countAll(id);
    }

    long countAllByStatus(boolean status)
    {
        return psspUserRepo.countAllByStatus(status);
    }

    public Page<PsspUser> doSearch(UsersSearchRequest req, Pageable pageable) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            boolean statusVar;

            //status provided but date_created NOT provided
            if (!StringUtils.isEmpty(req.getActivated()) && StringUtils.isEmpty(req.getCreatedAt())) {
                statusVar = Boolean.parseBoolean(req.getActivated());
                return psspUserRepo.search(req.getEmail(), req.getRole(), statusVar, pageable);
            }

            //date_created provided but status NOT provided
            if (!StringUtils.isEmpty(req.getCreatedAt()) && StringUtils.isEmpty(req.getActivated())) {
                Date from = CommonUtils.startOfDay(dateFormat.parse(req.getCreatedAt()));
                Date to = CommonUtils.endOfDay(dateFormat.parse(req.getCreatedAt()));
                return psspUserRepo.search(req.getEmail(),req.getRole(),from,to,pageable);
            }

            //created date and status are provided
            if(!StringUtils.isEmpty(req.getCreatedAt()) && !StringUtils.isEmpty(req.getActivated())){
                statusVar = Boolean.parseBoolean(req.getActivated());
                Date from = CommonUtils.startOfDay(dateFormat.parse(req.getCreatedAt()));
                Date to = CommonUtils.endOfDay(dateFormat.parse(req.getCreatedAt()));
                return psspUserRepo.search(req.getEmail(),req.getRole(),from,to,statusVar,pageable);
            }

            return psspUserRepo.search(req.getEmail(),req.getRole(),pageable);


        } catch (ParseException ex) {
            LOG.error("Date format incorrect ",ex.getMessage());
        }catch(Exception e){
            LOG.error("Unknown error occured ",e);
        }

        return new PageImpl<>(new ArrayList<>(), pageable,0);

    }

}
