package ng.upperlink.nibss.cmms.service.nibss;

import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.nibss.NibssRequest;
import ng.upperlink.nibss.cmms.dto.search.UsersSearchRequest;
import ng.upperlink.nibss.cmms.embeddables.Name;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.enums.makerchecker.ViewAction;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.NibssUser;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.repo.nibss.NibssUserRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.contact.CountryService;
import ng.upperlink.nibss.cmms.service.contact.LgaService;
import ng.upperlink.nibss.cmms.service.contact.StateService;
import ng.upperlink.nibss.cmms.service.makerchecker.UserAuthorizationService;
import ng.upperlink.nibss.cmms.util.CommonUtils;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;
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
public class NibssUserService {

    private static Logger LOG = LoggerFactory.getLogger(NibssUserService.class);

    private RoleService roleService;
    private NibssUserRepo nibssUserRepo;
    private LgaService lgaService;
    private StateService stateService;
    private CountryService countryService;
    private UserService userService;
    private SmtpMailSender smtpMailSender;
    private UserType userType = UserType.NIBSS;
    private UserAuthorizationService userAuthorizationService;

    @Value("${encryption.salt}")
    private String salt;

    @Autowired
    public void setUserAuthorizationService(UserAuthorizationService userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
    }

    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setNibssUserRepo(NibssUserRepo nibssUserRepo) {
        this.nibssUserRepo = nibssUserRepo;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setLgaService(LgaService lgaService) {
        this.lgaService = lgaService;
    }

    @Autowired
    public void setStateService(StateService stateService) {
        this.stateService = stateService;
    }

    @Autowired
    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public Page<NibssUser> get(Pageable pageable) {
        return nibssUserRepo.getAll(UserType.NIBSS, AuthorizationStatus.CREATION_REJECTED, pageable);
    }

    public NibssUser get(Long id) {
        return nibssUserRepo.get(id, userType);
    }

    public NibssUser save(NibssUser nibss) {
        return nibssUserRepo.save(nibss);
    }

    public void validate(NibssRequest request, boolean isUpdate, Long id) throws CMMSException {
        NibssUser nibss = null;
        if (isUpdate)
            if (id == null) {
                throw new CMMSException("NibssUser id is not provided", "400", "400");
            }
        userService.validate(userService.getUserRequest(request), isUpdate, request.getUserId());
    }

    public NibssUser generate(NibssUser newNibbsUser, NibssUser existingUser, NibssRequest request, User operator, boolean isUpdate, UserType userType) throws IOException, CMMSException {
        newNibbsUser = generateUpdate(newNibbsUser, request);
        Role newUserRole = newNibbsUser.getRoles().stream().findAny().get();
        if (isUpdate) {
            authenticate(newUserRole, operator, null, InitiatorActions.UPDATE);
            //newNibbsUser.setUpdatedBy(userService.setUser(operator));
            newNibbsUser.setUpdatedBy(new User(operator.getId(),operator.getName(),operator.getEmailAddress(),operator.getUserType()));
            String jsonData = JsonBuilder.generateJson(newNibbsUser);
            return (NibssUser) userAuthorizationService.actions(jsonData, existingUser, operator, null, InitiatorActions.UPDATE, null);

        } else {
            newNibbsUser.setUserType(userType);
            newNibbsUser.setChange_password(true);
            newNibbsUser.setCreatedBy(userService.setUser(operator));
            newNibbsUser.setActivated(false);
            newNibbsUser.setStaffNumber(request.getStaffNumber());
            newNibbsUser.setCreatedAt(new Date());
            authenticate(newUserRole, operator, null, InitiatorActions.CREATE);
            return (NibssUser) userAuthorizationService.actions(null, newNibbsUser, operator, null, InitiatorActions.CREATE, null);
        }
    }


    public NibssUser generateUpdate(NibssUser newNibbsUser, NibssRequest userRequest) throws IOException {
        newNibbsUser.setContactDetails(userService.getContactDetails(userRequest));
        newNibbsUser.setEmailAddress(userRequest.getEmailAddress());
        newNibbsUser.setName(new Name(userRequest.getName()));
        newNibbsUser.setPhoneNumber(userRequest.getPhoneNumber());
        newNibbsUser.setUserType(userType);
        Collection<Role> newRoles = this.roleService.getActivated(userRequest.getRoleId());
        newNibbsUser.getRoles().addAll(newRoles);
        return newNibbsUser;
    }

    public NibssUser generateApproved(NibssUser nibssUser, NibssUser fromJson, User operator) throws IOException {
        nibssUser.setContactDetails(fromJson.getContactDetails());
        nibssUser.setEmailAddress(fromJson.getEmailAddress());
        nibssUser.setName(fromJson.getName());
        nibssUser.setPhoneNumber(fromJson.getPhoneNumber());
        // delete old roles and persist new
        Set<Role> oldRoles = nibssUser.getRoles();
        if (oldRoles != null) {
            nibssUser.getRoles().removeAll(nibssUser.getRoles());
        }
        nibssUser.setUpdatedAt(fromJson.getUpdatedAt());
        nibssUser.setUpdatedBy(fromJson.getUpdatedBy());
        nibssUser.getRoles().addAll(fromJson.getRoles());
        System.out.println("To be saved:"+nibssUser.getId());
        return nibssUser;
    }

    public User toggle(NibssUser nibssUser, User operator, AuthorizationAction authorizationAction, InitiatorActions initiatorActions) throws IOException, CMMSException {
        Role newUserRole = nibssUser.getRoles().stream().findAny().get();
        authenticate(newUserRole, operator, null, InitiatorActions.TOGGLE);
        nibssUser = (NibssUser) userAuthorizationService.actions(null, nibssUser, operator, null, initiatorActions, null);
        if (nibssUser != null)
            return this.save(nibssUser);
        else {
            throw new CMMSException("Could not toggleInit", "500", "500");
        }

    }

    public void sendAwarenessEmail(NibssUser nibssUser, String password, String loginURL, boolean isUpdated, String fromEmail) {


        if (nibssUser.getCreatedBy() == null) {
            LOG.error("NIbss User 'created by' is NULL");
            return;
        }

        if (isUpdated && nibssUser.getUpdatedBy() == null) {
            LOG.error("Nibss User 'updated by' is NULL");
            return;
        }
        String emailAddress = nibssUser.getEmailAddress();

        String[] email = {emailAddress};

        if (email.length > 0) {
            String subject = "Login Credentials";
            String title = "Account Creation";
            String message = "Your account in Central Mandate Management System (CMMS) has been Created. Please you can Login to change your password.";
            if (isUpdated) {
                message = "Changes on your account have been updated";
                subject = "Password reset";
                title = "Account Update";
            }
            //Send the mail
            smtpMailSender.sendMail(fromEmail, email, subject,
                    title, message, generateDetails(nibssUser, isUpdated, password, loginURL));
        }

    }

    private String generateDetails(NibssUser nibssUser, boolean isUpdate, String password, String loginURL) {

        String details = "";
        if (isUpdate) {
            details += "<strong>Your password reset successful : </strong> " + "<br/>";
            details += "<strong>Date : </strong>  " + nibssUser.getUpdatedAt() + "<br/>";
        } else {
            details += "<strong>Username:</strong> " + nibssUser.getEmailAddress() + "<br/>";
            details += "<strong>Password:</strong> " + password + "<br/>";
        }
        return details;
    }

    public void authenticate(Role newUserRole, User operatorUser, AuthorizationAction action, InitiatorActions initiatorAction) throws CMMSException {
        if (operatorUser == null) {
            throw new CMMSException(Errors.NOT_PERMITTED.getValue(), "401", "401");
//            return ResponseEntity.status(404).body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        Collection<Role> roles = operatorUser.getRoles();
        if (roles == null) {
            throw new CMMSException(Errors.NOT_PERMITTED.getValue(), "401", "401");
//            return ResponseEntity.status(401).body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        Role operatorRole = roles.stream().findAny().get();

        RoleName[] adminRoleNames = {
                RoleName.NIBSS_INITIATOR,
                RoleName.NIBSS_AUTHORIZER
        };
//        RoleName[] adminRoleNames = {
//                RoleName.NIBSS_SUPER_ADMIN_AUTHORIZER,
//                RoleName.NIBSS_SUPER_ADMIN_INITIATOR};

        InitiatorActions[] initiatorActions = {
                InitiatorActions.CREATE,
                InitiatorActions.UPDATE,
                InitiatorActions.TOGGLE
        };
        AuthorizationAction[] authorizerActions = {
                AuthorizationAction.APPROVE_CREATE,
                AuthorizationAction.APPROVE_UPDATE,
                AuthorizationAction.APPROVE_TOGGLE,
                AuthorizationAction.REJECT_CREATE,
                AuthorizationAction.REJECT_UPDATE,
                AuthorizationAction.REJECT_TOGGLE
        };
        if (!Arrays.asList(adminRoleNames).contains(newUserRole.getName())&& !newUserRole.getName().equals(RoleName.NIBSS_USER)) {
            throw new CMMSException("Not authorize to act on the user with " + newUserRole.getName().getValue() + " role", "401", "401");
        }
        else if(Arrays.asList(adminRoleNames).contains(newUserRole.getName()) && Arrays.asList(initiatorActions).contains(initiatorAction))
            {
                if(!operatorRole.getName().equals(RoleName.NIBSS_SUPER_ADMIN_INITIATOR))
                    {
                        throw new CMMSException(Errors.NOT_PERMITTED_TO_CREATE.getValue().replace("{}",newUserRole.getName().getValue()), "401", "401");
                    }
            }
        else if(Arrays.asList(adminRoleNames).contains(newUserRole.getName()) && Arrays.asList(authorizerActions).contains(action))
            {
                if(!operatorRole.getName().equals(RoleName.NIBSS_SUPER_ADMIN_AUTHORIZER))
                    {
                        throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE.getValue().replace("{}",newUserRole.getName().getValue()), "401", "401");
                    }
            }
        else if(newUserRole.getName().equals(RoleName.NIBSS_USER) && Arrays.asList(initiatorActions).contains(initiatorAction))
            {
                if(!operatorRole.getName().equals(RoleName.NIBSS_INITIATOR))
                    {
                        throw new CMMSException(Errors.NOT_PERMITTED_TO_CREATE.getValue().replace("{}",newUserRole.getName().getValue()), "401", "401");
                    }
            }
        else if(newUserRole.getName().equals(RoleName.NIBSS_USER) && Arrays.asList(authorizerActions).contains(action))
            {
                if(!operatorRole.getName().equals(RoleName.NIBSS_AUTHORIZER))
                    {
                        throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE.getValue().replace("{}",newUserRole.getName().getValue()), "401", "401");
                    }
            }
    }

    public Page<NibssUser> selectView(ViewAction viewAction, Pageable pageable) {
        switch (viewAction) {
            case UNAUTHORIZED:
                return this.getAllPendingUsers(pageable);
            case AUTHORIZED:
                return getAllApprovedUsers(AuthorizationStatus.AUTHORIZED, pageable);
            case REJECTED:
                return getAllRejectedUsers(Arrays.asList(UserAuthorizationService.rejectionStatuses), pageable);
            default:
                return getAllApprovedUsers(AuthorizationStatus.AUTHORIZED, pageable);
        }
    }

    private Page<NibssUser> getAllPendingUsers(Pageable pageable) {
        return this.nibssUserRepo.getAllPendingUsers(Arrays.asList(UserAuthorizationService.pendingActions), pageable);
    }

    private Page<NibssUser> getAllApprovedUsers(AuthorizationStatus authStatus, Pageable pageable) {
        return nibssUserRepo.getAllApprovedUsers(authStatus, pageable);
    }

    private Page<NibssUser> getAllRejectedUsers(List<AuthorizationStatus> authStatusList, Pageable pageable) {
        return nibssUserRepo.getAllRejectedUsers(authStatusList, pageable);
    }

    public NibssUser performAuthorization(AuthorizationRequest request, AuthorizationAction action, UserDetail userDetail) throws CMMSException {
        NibssUser user;
        request.setAuthorization(action);
        user = (NibssUser) userAuthorizationService.performAuthorization(request, action, userDetail);
//            user = save(user);
        return user;
//            nibss = (NibssUser)nibssUserService.save(nibss);
    }
    private ResponseEntity authorizationAuth(AuthorizationRequest request, UserDetail userDetail, AuthorizationAction action) {
        NibssUser nibss;
        User operatorUser = null;
        try {
            //get the user logged in
            if (request.getId() != null) {
                operatorUser = userService.get(userDetail.getUserId());
                if (operatorUser == null)
                    return ErrorDetails.setUpErrors("Authorization failed", Arrays.asList(Errors.UNKNOWN_USER.getValue()),"404");
            }

            if (action == null) {
                return ErrorDetails.setUpErrors("Authorization failed", Arrays.asList("Please specify the action"),"400");
            }
            request.setAuthorization(action);
            //get the user to be updated
            nibss = this.get(request.getId());
            if (nibss == null)
                return ErrorDetails.setUpErrors("Authorization failed", Arrays.asList("Invalid Id provided."),"404");

            nibss = (NibssUser) userAuthorizationService.actions(null, nibss, operatorUser, action, null, request);

            return ResponseEntity.ok(nibss);
//            nibss = (NibssUser)nibssUserService.save(nibss);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Authorization failed", Arrays.asList(e.getMessage()),e.getCode());
        }catch(Exception ex){
            LOG.error("Authorization failed ",ex);
            return ErrorDetails.setUpErrors("Authorization failed", Arrays.asList(ex.getMessage()),"500");
        }
    }

    public NibssUser previewUpdate(Long id) throws CMMSException {
        NibssUser fromJson = nibssUserRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        if (fromJson == null) {
            throw new CMMSException(Errors.NO_UPDATE_REQUEST.getValue(), "404", "404");
        }
        String jsonData = fromJson.getJsonData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            NibssUser jsonUser = mapper.readValue(jsonData, NibssUser.class);
            return jsonUser;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CMMSException(e.getMessage(), "500", "500");
        }
    }

    public Long getAllNIBSSUsers(){
        return nibssUserRepo.getAllNIBSSUsers().stream().count();
    }

    public Long getUsersByActiveStatus(boolean activeStatus){
        return nibssUserRepo.getAllActivated(UserType.NIBSS,activeStatus).stream().count();
    }

    public Page<NibssUser> doSearch(UsersSearchRequest req, Pageable pageable) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            boolean statusVar;

            //status provided but date_created NOT provided
            if (!StringUtils.isEmpty(req.getActivated()) && StringUtils.isEmpty(req.getCreatedAt())) {
                statusVar = Boolean.parseBoolean(req.getActivated());
                return nibssUserRepo.search(req.getEmail(), req.getRole(), statusVar, pageable);
            }

            //date_created provided but status NOT provided
            if (!StringUtils.isEmpty(req.getCreatedAt()) && StringUtils.isEmpty(req.getActivated())) {
                Date from = CommonUtils.startOfDay(dateFormat.parse(req.getCreatedAt()));
                Date to = CommonUtils.endOfDay(dateFormat.parse(req.getCreatedAt()));
                return nibssUserRepo.search(req.getEmail(),req.getRole(),from,to,pageable);
            }

            //created date and status are provided
            if(!StringUtils.isEmpty(req.getCreatedAt()) && !StringUtils.isEmpty(req.getActivated())){
                statusVar = Boolean.parseBoolean(req.getActivated());
                Date from = CommonUtils.startOfDay(dateFormat.parse(req.getCreatedAt()));
                Date to = CommonUtils.endOfDay(dateFormat.parse(req.getCreatedAt()));
                return nibssUserRepo.search(req.getEmail(),req.getRole(),from,to,statusVar,pageable);
            }

            return nibssUserRepo.search(req.getEmail(),req.getRole(),pageable);


        } catch (ParseException ex) {
            LOG.error("Date format incorrect ",ex.getMessage());
        }catch(Exception e){
            LOG.error("Unknown error occured ",e);
        }

        return new PageImpl<>(new ArrayList<>(), pageable,0);

    }


    public Page<NibssUser> getNibssUsersPageable(Pageable pageable){
        return nibssUserRepo.getNIBSSUserPageable(pageable);
    }

}
