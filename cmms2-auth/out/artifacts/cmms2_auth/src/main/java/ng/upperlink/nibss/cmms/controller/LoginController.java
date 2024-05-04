package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.config.cache.JWTRedisToken;
import ng.upperlink.nibss.cmms.config.cache.SessionManager;
import ng.upperlink.nibss.cmms.config.cache.UserLoginCacheService;
import ng.upperlink.nibss.cmms.config.cache.UserTokenCacheService;
import ng.upperlink.nibss.cmms.dto.*;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.MakerCheckerType;
import ng.upperlink.nibss.cmms.enums.SecurityConstants;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.*;
import ng.upperlink.nibss.cmms.model.Role;
import ng.upperlink.nibss.cmms.service.*;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by stanlee on 07/04/2018.
 */
//@ApiIgnore
@RestController
public class LoginController {

    private static Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private UserService userService;

    private NibssService nibssService;

    private SubscriberService subscriberService;

    private JWTRedisToken jwtRedisToken;

    private UserTokenCacheService userTokenCacheService;

    private UserLoginCacheService userLoginCacheService;

    private PasswordValidationService passwordValidationService;

    private SessionManager sessionManager;

    @Value("${encryption.salt}")
    private String salt;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setNibssService(NibssService nibssService) {
        this.nibssService = nibssService;
    }

    @Autowired
    public void setSubscriberService(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Autowired
    public void setJwtRedisToken(JWTRedisToken jwtRedisToken) {
        this.jwtRedisToken = jwtRedisToken;
    }

    @Autowired
    public void setUserTokenCacheService(UserTokenCacheService userTokenCacheService) {
        this.userTokenCacheService = userTokenCacheService;
    }

    @Autowired
    public void setUserLoginCacheService(UserLoginCacheService userLoginCacheService) {
        this.userLoginCacheService = userLoginCacheService;
    }

    @Autowired
    public void setPasswordValidationService(PasswordValidationService passwordValidationService) {
        this.passwordValidationService = passwordValidationService;
    }

    @Autowired
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest request, HttpSession httpSession){

        if (userLoginCacheService.isUserLogged(request.getEmailAddress())){
            //get the old user detail.
            String userSession = userLoginCacheService.getLoggedUserSession(request.getEmailAddress());
            //update the login cache
            userLoginCacheService.setUserAsNotLogged(request.getEmailAddress());
            //remove from redis
            userTokenCacheService.deleteUserToken(userSession);
            //delete from session cache
            sessionManager.deleteSession(userSession);
        }

        User user = userService.getByEmail(request.getEmailAddress());
        if (user == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Invalid credential"));
        }

        if (user.getPassword() == null || !EncyptionUtil.doSHA512Encryption(request.getPassword(), salt).equals(user.getPassword())){
            return ResponseEntity.badRequest().body(new ErrorDetails("Invalid credential"));
        }

        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(user.getId());
        userDetail.setEmailAddress(user.getEmailAddress());
        userDetail.setRoles(user.getRoles().stream().map(r->r.getName()).collect(Collectors.toSet()));
        userDetail.setSessionId(httpSession.getId());
        userDetail.setUserType(user.getUserType().getValue());
        String token = "";

        switch (user.getUserType()){

            case NIBSS:
                NibssUser nibss = nibssService.getByUserId(user.getId());
                if (null == nibss) {
                    return ResponseEntity.badRequest().body(new ErrorDetails("This account have been disabled. Please contact administrator"));
                }
                userDetail.setCode(nibss.getStaffNumber());
                userDetail.setUserAuthorizationType(nibss.getMakerCheckerType().getValue());
                token = jwtRedisToken.generateToken(userDetail);

                userTokenCacheService.saveUserTokenAndTask(userDetail.getSessionId(),token, getTask(user.getRoles().stream().flatMap(r->r.getPrivileges().stream()).collect(Collectors.toSet())));
                setAsLoggedIn(user, userDetail.getSessionId());
                for(Role role:  nibss.getRoles())
                    role.setPrivileges(null);
                return ResponseEntity.ok(new LoginResponse(nibss, token));

             /*case BANK:
               /* Agent agent = agentService.getByUserId(user.getId());
                if (null == agent) {
                    return ResponseEntity.badRequest().body(new ErrorDetails("This account have been disabled. Please contact administrator"));
                }
                userDetail.setCode(agent.getCode());
                userDetail.setUserAuthorizationType(agent.getMakerCheckerType().getValue());
                userDetail.setInstitutionCode(agentService.getInstitutionCodeById(agent.getId()));
                token = jwtRedisToken.generateToken(userDetail);
                userTokenCacheService.saveUserTokenAndTask(userDetail.getSessionId(),token, getTask(user.getRole().getTasks()));

                setAsLoggedIn(user, userDetail.getSessionId());

                agent.getRole().setTasks(null);
                return ResponseEntity.ok(new LoginResponse(agent, token));*/

             /*case PSSP:
                Enroller enroller = enrollerService.getByUserId(user.getId());
                if (null == enroller) {
                    return ResponseEntity.badRequest().body(new ErrorDetails("This account have been disabled. Please contact administrator"));
                }
                userDetail.setCode(enroller.getCode());
                userDetail.setUserAuthorizationType(MakerCheckerType.OPERATOR.getValue());
                userDetail.setInstitutionCode(enrollerService.getInstitutionCodeById(enroller.getId()));
                token = jwtRedisToken.generateToken(userDetail);
                userTokenCacheService.saveUserTokenAndTask(userDetail.getSessionId(),token, getTask(user.getRole().getTasks()));

                setAsLoggedIn(user, userDetail.getSessionId());

                enroller.getRole().setTasks(null);
                return ResponseEntity.ok(new LoginResponse(enroller, token));*/

             /*case BILLER:
               AgentManager agentManager = subscriberService.getByUserId(user.getId());
                if (null == agentManager) {
                    return ResponseEntity.badRequest().body(new ErrorDetails("This account have been disabled. Please contact administrator"));
                }
                userDetail.setCode(agentManager.getCode());
                userDetail.setUserAuthorizationType(agentManager.getMakerCheckerType().getValue());
                userDetail.setInstitutionCode(agentManager.getAgentManagerInstitution().getCode());
                token = jwtRedisToken.generateToken(userDetail);
                userTokenCacheService.saveUserTokenAndTask(userDetail.getSessionId(),token, getTask(user.getRole().getTasks()));

                setAsLoggedIn(user, userDetail.getSessionId());

                agentManager.getRole().setTasks(null);
                return ResponseEntity.ok(new LoginResponse(agentManager, token));*/

            /*case SUBSCRIBER:
                Agent agent = agentService.getByUserId(user.getId());
                if (null == agent) {
                    return ResponseEntity.badRequest().body(new ErrorDetails("This account have been disabled. Please contact administrator"));
                }
                userDetail.setCode(agent.getCode());
                userDetail.setUserAuthorizationType(agent.getMakerCheckerType().getValue());
                userDetail.setInstitutionCode(agentService.getInstitutionCodeById(agent.getId()));
                token = jwtRedisToken.generateToken(userDetail);
                userTokenCacheService.saveUserTokenAndTask(userDetail.getSessionId(),token, getTask(user.getRole().getTasks()));

                setAsLoggedIn(user, userDetail.getSessionId());

                agent.getRole().setTasks(null);
                return ResponseEntity.ok(new LoginResponse(agent, token));*/

            default:
                LOG.error("No user Type found for user Id => {}, user email address",user.getId(), user.getEmailAddress());
                return ResponseEntity.badRequest().body(new ErrorDetails("Invalid credential"));
        }
    }

    @GetMapping("/user/logout")
    public ResponseEntity logout(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail, @ApiIgnore HttpServletRequest request){

        //update the login cache
        userLoginCacheService.setUserAsNotLogged(userDetail.getEmailAddress());

        //remove from redis
        userTokenCacheService.deleteUserToken(request.getHeader(SecurityConstants.HEADER_STRING.getValue()), userDetail.getSessionId());
        return ResponseEntity.ok().body("Successfully logged out");
    }

    private void setAsLoggedIn(User user, String sessionId){
        //set logged in to true
        userLoginCacheService.setUserAsLogged(user.getEmailAddress(),sessionId);
    }


    private List<String> getTask(Set<Privilege> tasks){
        List<String> userTasks = new ArrayList<>();
        for (Privilege task : tasks) {
            userTasks.add(task.getName());
        }

        return userTasks;
    }

}
