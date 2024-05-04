package ng.upperlink.nibss.cmms.controller;

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
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class AuthenticationController {

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

    private static final String TEST_TOKEN = "0099887766";

    private static final String TEST_TOKEN_BEARER = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1OGE3YWM0ZWY3MTA3Mjk2ZDUxNjA2Y2U3YzRmNmJiMWU5ZTEwNjUwM2E3ODU3YTAwODFkYjBmZjE3MzcyOTA3ZGQxODdlNjk3NTIzZDM4ZTIyZDNlZjJjY2M3ZGU3NjlkOGVkZDZlMzIxOTBkMWE5YTAzYThlOWE0YzQxODlkNDhmNGRhZDE3ZmJhMzE0MWU1NmNkMWQwMDFmMWU5MTkzMzFjMDQyZWM2Y2FlODJjYjVlM2RhYTA5YzhkYjQzNjY1YWQ3YjhlNTU1NTBkNGYxYzFhMmQ1YTc1YzI2MjMwM2NmNjI5YzE1N2QxYWJmNDM0OTUyZmY4OGEyN2QyMjczYWY2M2UyNDZlMjhkMTg0M2IwNmY1YTNlN2M5ZTAxZjEiLCJpc3MiOiJodHRwOi8vMTAuNy43LjEwNDo4Ni8yRkFTZXJ2aWNlL2FwaS9hdXRoL2xvZ2luIiwiaWF0IjoxNTQ3MDM0NDQ2LCJleHAiOjE1NDcxMjA4NDZ9.qJd87zKt1sPJz9mI50KyNeuXiY_0WF4eZ6MPnI0oZxBORRzAyRvd-vnccnu-gFbU2gSCzfTuvdP0gnTebFLskA";

    private static final String TEST_REQUEST_ID = "20171012125506150780";

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

    private void setAsLoggedIn(User user, String sessionId) {
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

    @PostMapping("/authenticate")
    public ResponseEntity authenticate(@Valid @RequestBody LoginRequest request) {
        try{
            //check if the user is logged in
            if (userLoginCacheService.isUserLogged(request.getEmailAddress())) {
                log.info("User is still logged in");
                //get the old user detail.
                String userSession = userLoginCacheService.getLoggedUserSession(request.getEmailAddress());

                log.info("User session => {} ", userSession);
                //update the login cache
                userLoginCacheService.setUserAsNotLogged(request.getEmailAddress());
                //remove from redis
                userTokenCacheService.deleteUserToken(userSession);
                //delete from session cache
                sessionManager.deleteSession(userSession);
            }

            //validate email and password
            User userLoggedIn = userService.getByEmail(request.getEmailAddress());
            if (userLoggedIn == null) {
                return ErrorDetails.setUpErrors("Log in failed", Arrays.asList("Invalid credential"), "404");
            }

            if (userLoggedIn.getPassword() == null || !EncyptionUtil.doSHA512Encryption(request.getPassword(), salt).equals(userLoggedIn.getPassword())) {
                return ErrorDetails.setUpErrors("Log in failed", Arrays.asList("Invalid credential"), "404");
            }

            String name = userLoggedIn.getName().getLastName() == null ? "user" : userLoggedIn.getName().getLastName();

            String[] bearerKey = this.generateTokenBearer();

            String[] emailToken = generateEmailToken(this.user, userLoggedIn.getEmailAddress(), userLoggedIn.getPhoneNumber(), name, bearerKey[1]);

            log.info("Email Token = {}", emailToken[1]);

            //email token generated
            if (emailToken[0].equals(OK_RESPONSE)) {
                return ResponseEntity.ok(new LoginToken(userLoggedIn.getId(), emailToken[1], emailToken[2], bearerKey[1], "Token has been sent to your mail. Please check and renter to complete login."));
            }

       //return ResponseEntity.ok(new LoginToken(userLoggedIn.getId(), null, TEST_REQUEST_ID, TEST_TOKEN_BEARER, "Token has been sent to your mail. Please check and renter to complete login."));
        }catch(Exception ex){
            log.error("Exception thrown ",ex);
            new ResponseEntity<>(new ErrorDetails(new Date(), "Login Failed", "Could not complete request processing."), HttpStatus.BAD_REQUEST);
           // return ErrorDetails.setUpErrors("Login Failed.", Arrays.asList(ex.getMessage()),ex.getCode());
        }

     return new ResponseEntity<>(new ErrorDetails(new Date(), "Login Failed", "Could not complete request processing."), HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/emailToken")
    public ResponseEntity tryLogin(@RequestBody TokenBuilder tokenBuilder, HttpSession httpSession) {
        try {
            User user = userService.get(tokenBuilder.getUserId());
            if (user == null) {
                return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
            }

            boolean verified = doVerifyToken(tokenBuilder.getSoftToken(), tokenBuilder.getRequestId(), user.getEmailAddress(), tokenBuilder.getSessionKey());

            if (verified) {
                return doLogin(user, httpSession);
            }


        } catch (Exception secExcption) {
            log.error("Security Exception thrown => ", secExcption);
        }

        return ResponseEntity.badRequest().body("Could not complete login request.");
    }

    public boolean doVerifyToken(String token, String requestId, String tokenFor, String sessionKey) throws SecException {

        SecondFactorClient secondFactorClient = new SecondFactorClient();
        TokenVerification tokenVerification = new TokenVerification(token, requestId, tokenFor, "LOGIN");

        AESCrypter aesCrypter = new AESCrypter(hash, key);

        String enc = aesCrypter.encrypt(tokenVerification.toJsonObject().toString());

        log.info("Encrypted info = {} ", enc);

        Response post = secondFactorClient.getTargetBaseUrl().path("/do2FA/vfy").register(new ClientFilter(user, "", "", "", "", "", sessionKey)).request(MediaType.TEXT_PLAIN).post(Entity.text(enc), Response.class);

        log.info("Status = {} ", post.getStatus());

        if (200 == post.getStatus()) {
            log.trace("Verification was successful");
            return true;
        } else {
            log.trace("Verification failed");
            return false;
            //throw new SecException("");
        }

    }

    public String[] generateTokenBearer() throws SecException{

        String[] resp = {"06", "NA"};
        try {
            SecondFactorClient secondFactorClient = new SecondFactorClient();
            JsonObject build = Json.createObjectBuilder().add("user", user).add("password", hash).add("randomTok", key).build();
            Response post = secondFactorClient.getTargetBaseUrl().path("/auth/login").request().post(Entity.json(build));

            log.info("Status code = {}", post.getStatus());

            if (post.getStatus() == 200) {
                resp[0] = "00";
                resp[1] = post.getHeaderString("Authorization");
                log.info("Token generated => ", post.getStatus());
            }else {
                throw new SecException(String.valueOf(post.getStatus()),TokenKeyMessage.findById(String.valueOf(post.getStatus())).getValue());
            }

        } catch (Exception e) {
            log.error("Exception thrown while generating token ", e);
        }
        return resp;
    }


    public String[] generateEmailToken(String user, String emails, String phoneNo, String receipient, String sessionKey) throws SecException{

        String[] responseArray = {"06", "NA", ""};
        try {
            SecondFactorClient secondFactorClient = new SecondFactorClient();
            String requestId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "" + System.currentTimeMillis();

            JsonObject build = Json.createObjectBuilder().add("requestId", requestId.substring(0, 20)).add("emails", emails).add("phoneNo", phoneNo).add("recipient", receipient).add("requestType", "LOGIN").build();

            AESCrypter aesCrypter = new AESCrypter(hash, key);
            String enc = aesCrypter.encrypt(build.toString());

            log.info("encrypted info {} ", enc);

            Response post = secondFactorClient.getTargetBaseUrl().path("/do2FA/emailToken").register(new ClientFilter(user, "", "", "", "", "", sessionKey)).request(MediaType.TEXT_PLAIN).post(Entity.text(aesCrypter.encrypt(build.toString())), Response.class);


            log.info(" *** post = " + post.readEntity(String.class));

            if (post.getStatus() == 200) {
                responseArray[0] = OK_RESPONSE;
                responseArray[1] = enc;
                responseArray[2] = requestId.substring(0, 20);
            } else {
                throw new SecException(String.valueOf(post.getStatus()),TokenKeyMessage.findById(String.valueOf(post.getStatus())).getValue());
            }

        } catch (Exception e) {
            log.error("Exception thrown while sending mail token => ", e);
        }

        return responseArray;
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

    public ResponseEntity doLogin(User user, HttpSession httpSession) {
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
            case NIBSS:
                NibssUser nibss = nibssService.get(user.getId());


                if (nibss == null) {
                    return ErrorDetails.setUpErrors("Log in failed", Arrays.asList("User is unknown"), "404");
                }

                if (!nibss.isActivated()) {
                    return ErrorDetails.setUpErrors("Login not authorized", Arrays.asList("Please contact administrator to activate your account"), "401");
                }

                token = cacheDetail(user, userDetail);
                break;
            case BANK:
                BankUser bankUser = bankUserService.getById(user.getId());

                if (bankUser == null) {
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
                    return ErrorDetails.setUpErrors("Log in failed", Arrays.asList("User is unknown"), "404");
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
                    return ErrorDetails.setUpErrors("Login failed", Arrays.asList("This account has been disabled. Please contact the administrator."), "404");
                }

                userDetail.setUserAuthorizationType(psspUser.getRoles().stream().map(Role::getUserAuthorisationType).collect(Collectors.toList()).get(0));

                PsspLoginDetails psspLoginDetails = new PsspLoginDetails(userDetail);


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

}
