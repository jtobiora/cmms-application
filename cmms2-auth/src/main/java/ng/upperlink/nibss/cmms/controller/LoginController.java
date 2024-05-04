package ng.upperlink.nibss.cmms.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.config.cache.JWTRedisToken;
import ng.upperlink.nibss.cmms.config.cache.SessionManager;
import ng.upperlink.nibss.cmms.config.cache.UserLoginCacheService;
import ng.upperlink.nibss.cmms.config.cache.UserTokenCacheService;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.auth.LoginRequest;
import ng.upperlink.nibss.cmms.dto.auth.LoginResponse;
import ng.upperlink.nibss.cmms.dto.bank.BankLoginDetails;
import ng.upperlink.nibss.cmms.dto.biller.BillerLoginDetails;
import ng.upperlink.nibss.cmms.dto.pssp.PsspLoginDetails;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.MakerCheckerType;
import ng.upperlink.nibss.cmms.enums.SecurityConstants;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.NibssUser;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Privilege;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.pssp.PsspUser;
import ng.upperlink.nibss.cmms.service.SubscriberService;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.PasswordValidationService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import ng.upperlink.nibss.cmms.service.nibss.NibssUserService;
import ng.upperlink.nibss.cmms.service.pssp.PsspUserService;
import ng.upperlink.nibss.cmms.tokenauth.*;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ng.upperlink.nibss.cmms.enums.UserType.BANK;

//@ApiIgnore
@RestController
@Slf4j
public class LoginController {

    //private static Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String OK_RESPONSE = "00";

    private UserService userService;

    private NibssUserService nibssService;

    private BankUserService bankUserService;

    private SubscriberService subscriberService;

    private JWTRedisToken jwtRedisToken;

    private UserTokenCacheService userTokenCacheService;

    private UserLoginCacheService userLoginCacheService;

    private PasswordValidationService passwordValidationService;

    private SessionManager sessionManager;

    private BankService bankService;

    private BillerUserService billerUserService;

    private PsspUserService psspUserService;

    @Value("${encryption.salt}")
    private String salt;
    @Value("${key}")
    private String key;
    @Value("${user}")
    private String user;
    @Value("${iv}")
    private String hash;

    @Autowired
    public void setPsspUserService(PsspUserService psspUserService) {
        this.psspUserService = psspUserService;
    }

    @Autowired
    public void setBillerUserService(BillerUserService billerUserService) {
        this.billerUserService = billerUserService;
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
    public void setNibssService(NibssUserService nibssService) {
        this.nibssService = nibssService;
    }

    @Autowired
    public void setBankUserService(BankUserService bankUserService) {
        this.bankUserService = bankUserService;
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
    public ResponseEntity login(@Valid @RequestBody LoginRequest request, HttpSession httpSession) {

               //check if the user is logged in
        if (userLoginCacheService.isUserLogged(request.getEmailAddress())){
            log.info("User is still logged in");
            //get the old user detail.
            String userSession = userLoginCacheService.getLoggedUserSession(request.getEmailAddress());
            log.info("Usersession =>{} ",userSession);
            //update the login cache
            userLoginCacheService.setUserAsNotLogged(request.getEmailAddress());
            //remove from redis
            userTokenCacheService.deleteUserToken(userSession);
            //delete from session cache
            sessionManager.deleteSession(userSession);
        }

        User user = userService.getByEmail(request.getEmailAddress());
        if (user == null) {
            return ErrorDetails.setUpErrors("Log in failed", Arrays.asList("Invalid credential"), "404");
        }

        if (user.getPassword() == null || !EncyptionUtil.doSHA512Encryption(request.getPassword(), salt).equals(user.getPassword())) {
            return ErrorDetails.setUpErrors("Log in failed", Arrays.asList("Invalid credential"), "404");
        }

        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(user.getId());
        userDetail.setEmailAddress(user.getEmailAddress());
        userDetail.setSessionId(httpSession.getId());
        userDetail.setUserType(user.getUserType().getValue());
        userDetail.setRoleType(user.getRoles().stream().map(role -> role.getRoleType().getValue()).findAny().get());
        userDetail.setRoles(user.getRoles().stream().map(r -> r.getName().getValue()).collect(Collectors.toList()));
        List<String> roleCollection = new ArrayList<>();

        String roleName = user.getRoles().stream().map(r -> r.getName().getValue()).findAny().get();
        String roleAuthType = user.getRoles().stream().map(r -> r.getUserAuthorisationType()).findAny().get();
        String roleUserType = user.getRoles().stream().map(r -> r.getUserType()).findAny().get().getValue();
        roleCollection.add(roleName);
        roleCollection.add(roleAuthType);
        roleCollection.add(roleUserType);

        //userDetail.setRoles(user.getRoles().stream().map(r->r.getName()).collect(Collectors.toList()));
        userDetail.setRoles(roleCollection);
        Collection<Role> roles = user.getRoles();

        List<String> privileges = new ArrayList<>();
        for (Role role : roles) {
            privileges.addAll(role.getPrivileges().stream().map(p -> p.getName()).collect(Collectors.toList()));
        }

        userDetail.setPrivileges(privileges);
        String token = "";
        switch (user.getUserType()) {
//            case SYSTEM:
//                token =cacheDetail(user,userDetail);
//                break;

            case NIBSS:
                NibssUser nibss = nibssService.get(user.getId());


                if (nibss == null) {
//                   return ResponseEntity.status(404).body(new ErrorDetails("Log in failed: User is unknown"));
                    return ErrorDetails.setUpErrors("Log in failed", Arrays.asList("User is unknown"), "404");
                }

                if (!nibss.isActivated()) {
//                        new ResponseEntity<>(new ErrorDetails(. ), HttpStatus.UNAUTHORIZED);
                    return ErrorDetails.setUpErrors("Login not authorized", Arrays.asList("Please contact administrator to activate your account"), "401");
                }

//                userDetail.setUserAuthorizationType(nibss.getMakerCheckerType().getValue());
                token = cacheDetail(user, userDetail);
                break;
            case BANK:
                BankUser bankUser = bankUserService.getById(user.getId());

                if (bankUser == null) {
//                     return ResponseEntity.status(404).body(new ErrorDetails("Log in failed: User is unknown"));
                    return ErrorDetails.setUpErrors("Log in failed", Arrays.asList("User is unknown"), "404");
                }

                if (!bankUser.isActivated()) {
                    return ErrorDetails.setUpErrors("Login not authorized", Arrays.asList("Please contact administrator to activate your account"), "401");
                }

                userDetail.setUserAuthorizationType(bankUser.getRoles().stream().map(Role::getUserAuthorisationType).collect(Collectors.toList()).get(0));

                BankLoginDetails bankLoginDetails = new BankLoginDetails(userDetail);
                bankLoginDetails.setBank(bankUser.getUserBank());

                token = jwtRedisToken.generateToken(bankLoginDetails);
                userTokenCacheService.saveUserTokenAndTask(bankLoginDetails.getSessionId(),
                        token,
                        getTask(user.getRoles().stream().flatMap(r -> r.getPrivileges().stream()).collect(Collectors.toList())));

                setAsLoggedIn(user, bankLoginDetails.getSessionId());
                for (Role role : bankUser.getRoles())
                    role.setPrivileges(null);
                return ResponseEntity.ok(new LoginResponse(bankUser, token));

            case BILLER:
                BillerUser billerUser = billerUserService.getById(user.getId());

                if (billerUser == null) {
//                     return ResponseEntity.status(404).body(new ErrorDetails("Log in failed: User is unknown"));
                    return ErrorDetails.setUpErrors("Log in failed", Arrays.asList("User is unknown"), "404");
                }

                if (!billerUser.isActivated()) {
//                     return ResponseEntity.status(401).body("Login not authorized. Please contact administrator to activate your account");
                }
                userDetail.setUserAuthorizationType(billerUser.getRoles().stream().map(Role::getUserAuthorisationType).collect(Collectors.toList()).get(0));

                BillerLoginDetails billerLoginDetails = new BillerLoginDetails(userDetail);
                billerLoginDetails.setBiller(billerUser.getBiller());

                token = jwtRedisToken.generateToken(billerLoginDetails);
                userTokenCacheService.saveUserTokenAndTask(billerLoginDetails.getSessionId(), token,
                        getTask(user.getRoles().stream().flatMap(r -> r.getPrivileges().stream()).collect(Collectors.toList())));

                setAsLoggedIn(user, billerLoginDetails.getSessionId());
                for (Role role : billerUser.getRoles())
                    role.setPrivileges(null);
                return ResponseEntity.ok(new LoginResponse(billerUser, token));

            case PSSP:
                PsspUser psspUser = psspUserService.getById(user.getId());

                if (psspUser == null)
                    return ErrorDetails.setUpErrors("Log in failed", Arrays.asList("User is unknown"), "404");
                if (!psspUser.isActivated()) {
//                        return ResponseEntity.badRequest().body(new ErrorDetails("This account has been disabled. Please contact the administrator."));
                    return ErrorDetails.setUpErrors("Login failed", Arrays.asList("This account has been disabled. Please contact the administrator."), "404");
                }

                userDetail.setUserAuthorizationType(psspUser.getRoles().stream().map(Role::getUserAuthorisationType).collect(Collectors.toList()).get(0));

                PsspLoginDetails psspLoginDetails = new PsspLoginDetails(userDetail);
                // psspLoginDetails.setBank(psspUser.getUserBank());

                token = jwtRedisToken.generateToken(psspLoginDetails);
                userTokenCacheService.saveUserTokenAndTask(psspLoginDetails.getSessionId(),
                        token,
                        getTask(user.getRoles().stream().flatMap(r -> r.getPrivileges().stream()).collect(Collectors.toList())));

                setAsLoggedIn(user, psspLoginDetails.getSessionId());
                for (Role role : psspUser.getRoles()) {
                    role.setPrivileges(null);
                }

                return ResponseEntity.ok(new LoginResponse(psspUser, token));


            default: {
                log.error("No user Type found for user Id => {}, user email address", user.getId(), user.getEmailAddress());
                return ResponseEntity.badRequest().body(new ErrorDetails("Invalid credential"));
            }

        }
        return ResponseEntity.ok(new LoginResponse(user, token));

    }

    private String cacheDetail(User user, UserDetail userDetail) {
        String token;
        token = jwtRedisToken.generateToken(userDetail);
        userTokenCacheService.saveUserTokenAndTask(userDetail.getSessionId(),
                token,
                getTask(user.getRoles().stream().flatMap(r -> r.getPrivileges().stream()).collect(Collectors.toList())));
        setAsLoggedIn(user, userDetail.getSessionId());
        for (Role role : user.getRoles())
            role.setPrivileges(null);
        return token;
    }

    @GetMapping("/user/logout")
    public ResponseEntity logout(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail, @ApiIgnore HttpServletRequest request) {

        //update the login cache
        userLoginCacheService.setUserAsNotLogged(userDetail.getEmailAddress());

        //remove from redis
        userTokenCacheService.deleteUserToken(request.getHeader(SecurityConstants.HEADER_STRING.getValue()), userDetail.getSessionId());
        return ResponseEntity.ok().body("Successfully logged out");
    }

    private void setAsLoggedIn(User user, String sessionId){
        //set logged in to true
        userLoginCacheService.setUserAsLogged(user.getEmailAddress(), sessionId);
    }


    private List<String> getTask(List<Privilege> tasks) {
        List<String> userTasks = new ArrayList<>();
        for (Privilege task : tasks) {
            userTasks.add(task.getName());
        }

        return userTasks;
    }


}
