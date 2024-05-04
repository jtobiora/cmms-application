package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.dto.*;
import ng.upperlink.nibss.cmms.dto.auth.PasswordPolicyResponse;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.ServiceResponseCode;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.PasswordLog;
import ng.upperlink.nibss.cmms.model.auth.PasswordRecoveryCode;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.PasswordRecoveryCodeService;
import ng.upperlink.nibss.cmms.service.auth.PasswordValidationService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import ng.upperlink.nibss.cmms.service.nibss.NibssUserService;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by stanlee on 07/04/2018.
 */
@RestController
@RequestMapping("/password")
public class PasswordController {

    private static Logger LOG = LoggerFactory.getLogger(PasswordController.class);
    private BillerUserService billerUserService;
    private BankUserService bankUserService;
    private UserService userService;
    private NibssUserService nibssUserService;
    @Autowired
    private PasswordValidationService passwordValidationService;

    @Autowired
    private PasswordRecoveryCodeService passwordRecoveryCodeService;

    @Value("${encryption.salt}")
    private String salt;

    @Autowired
    public void setBankUserService(BankUserService bankUserService){this.bankUserService =bankUserService;}
    @Autowired
    public void setBillerUserService(BillerUserService billerUserService){
        this.billerUserService = billerUserService;
    }
    @Autowired
    public void setNibssUserService(NibssUserService nibssUserService){
        this.nibssUserService = nibssUserService;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // Rest User's Password
    @PostMapping("/reset")
    public ResponseEntity resetUserPassword(@Valid @RequestBody ResetUserPasswordRequest request) {
        User user = userService.getByEmail(request.getEmail());
        if (null == user) {
            return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.USER_DOES_EXIST, ServiceResponseCode.USER_DOES_EXIST_MSG));
        } else {
            String recoveryCode = EncyptionUtil.generateString(20, true, true);
            if (null == passwordRecoveryCodeService.save(recoveryCode, user)) {
                return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.PASSWORD_RECOVERY_CODE_ERROR, ServiceResponseCode.PASSWORD_RECOVERY_CODE_ERROR_MSG));
            }
            // send a mail to the user
            userService.sendRecoveryEmail(user.getEmailAddress(), (request.getPath() + recoveryCode));
            return ResponseEntity.ok().body(new ServiceResponse(ServiceResponseCode.VALID, ServiceResponseCode.PASSWORD_RESET_SUCCESSFUL));
        }
    }


    // Validate recovery code
    @PostMapping("/validate-recovery-code")
    public ResponseEntity validateRecoveryCode(@Valid @RequestBody RecoveryCodeValidationRequest request) {
        PasswordRecoveryCode passwordRecoveryCode = passwordRecoveryCodeService.findRecoveryCode(request.getRecoveryCode());
        if (null == passwordRecoveryCode) {
            return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.RECOVERY_CODE_DOES_NOT_EXIST, ServiceResponseCode.RECOVERY_CODE_DOES_NOT_EXIST_MSG));
        }

        // check if this recovery code has been used before
        if (passwordRecoveryCode.isStatus()) {
            return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.RECOVERY_CODE_USED, ServiceResponseCode.RECOVERY_CODE_USED_MSG));
        }

        // check if the recovery code is more than a day
        if (passwordRecoveryCodeService.checkValidityPeriod(passwordRecoveryCode.getCreatedAt()) >= 1) {
            return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.RECOVERY_CODE_EXPIRED, ServiceResponseCode.RECOVERY_CODE_EXPIRED_MSG));
        }

        // update recovery code to avoid future usage
        passwordRecoveryCodeService.updateRecoveryCode(passwordRecoveryCode);
        return ResponseEntity.ok().body(new ServiceResponse(ServiceResponseCode.VALID, ServiceResponseCode.RECOVERY_CODE_VALID));
    }

    // Update user's password
    @PutMapping("/update")
    public ResponseEntity updateUserPasswordByRecoveryCode(@Valid @RequestBody UpdateUserPasswordRequest request)
    {
        PasswordRecoveryCode passwordRecoveryCode = passwordRecoveryCodeService.findRecoveryCode(request.getRecoveryCode());
        if (null == passwordRecoveryCode)
        {
            return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.USER_DOES_EXIST, ServiceResponseCode.USER_DOES_EXIST_MSG));
        } else
            {
            // check password validity
            PasswordPolicyResponse passwordPolicyResponse = passwordValidationService.isValid(passwordRecoveryCode.getUser().getEmailAddress(), request.getNewPassword());
            if (null != passwordPolicyResponse && null != passwordPolicyResponse.getResponseCode() && passwordPolicyResponse.getResponseCode().equals("00"))
            {
                User user = passwordRecoveryCode.getUser();
                user.setPassword(EncyptionUtil.doSHA512Encryption(request.getNewPassword(), salt));
                if (user.isChange_password())
                    user.setChange_password(false);
                try {
                    if (null == userService.save(user))
                        return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.UNABLE_TO_UPDATE_PASSWORD,
                                ServiceResponseCode.UNABLE_TO_UPDATE_PASSWORD_MSG));
                    else
                        return ResponseEntity.ok().body(new ServiceResponse(ServiceResponseCode.VALID, ServiceResponseCode.PASSWORD_UPDATED));
                } catch (CMMSException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(Integer.valueOf(e.getCode())).body(e.getMessage());
                }
            } else
                {
                return ResponseEntity.badRequest().body(
                        new ServiceResponse(null != passwordPolicyResponse && null != passwordPolicyResponse.getResponseCode() ?
                                passwordPolicyResponse.getResponseCode() : "90",
                                null != passwordPolicyResponse && null != passwordPolicyResponse.getResponseMessage() ?
                                        passwordPolicyResponse.getResponseMessage() :
                                        "Something went wrong when trying to validate user's password, please try again"));
            }
        }
    }

    @PutMapping("/update-password")
    public ResponseEntity updatePassword(@Valid @RequestBody UpdatePasswordRequest request,
                                         @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//        switch (userDetail.getUserType())
//        {
//            case "BANK": BankUser bankUser = bankUserService.getById(userDetail.getUserId());
//            break;
//            case "BILLER": BillerUser billerUser = billerUserService.getById(userDetail.getUserId());
//            break;
//            case "NIBSS": NibssUser nibssUser = nibssUserService.get(userDetail.getUserId());
//            break;
//            default: return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.USER_DOES_EXIST, ServiceResponseCode.USER_DOES_EXIST_MSG));
//        }
        // check password validity
        User user = userService.get(userDetail.getUserId());
        PasswordPolicyResponse passwordPolicyResponse = passwordValidationService.isValid(user.getEmailAddress(), request.getNewPassword());
        if (request.getNewPassword() !=null)
        {

            if (user == null)
            {
                return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.USER_DOES_EXIST, ServiceResponseCode.USER_DOES_EXIST_MSG));
            }
//            if (!user.getEmailAddress().equals(request.getEmailAddress()))
//            {
//                return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.UNABLE_TO_UPDATE_USER_PASSWORD, "You're not allowed to update another user's password"));
//
//            }
            if (!EncyptionUtil.doSHA512Encryption(request.getPassword(), salt).equals(user.getPassword()))
            {
                return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.PASSWORD_DOES_NOT_MATCH, ServiceResponseCode.PASSWORD_DOES_NOT_MATCH_MSG));
            }

            // update the last six used passwords
            String encryptedPassword = EncyptionUtil.doSHA512Encryption(request.getNewPassword(), salt);
            List<PasswordLog> passwordLogs = passwordValidationService.getUserPasswordLogs(user);
            if (passwordLogs.size() >= 6)
            {
                // get the oldest password
                PasswordLog oldestPasswordLog = passwordLogs.stream().max(Comparator.comparing(PasswordLog::getCreatedAt)).orElse(null);
                if (null == oldestPasswordLog)
                {
                    LOG.trace("Unable to locate the oldest password");
                    return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.UNABLE_TO_UPDATE_PASSWORD_LOG, ServiceResponseCode.UNABLE_TO_UPDATE_PASSWORD_LOG_MSG));
                } else
                    {
                    // update password log
                    PasswordLog updatedPasswordLog = passwordValidationService.updatePasswordLog(oldestPasswordLog, encryptedPassword);
                    if (null == updatedPasswordLog)
                    {
                        LOG.trace("Unable to update the oldest password");
                        return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.UNABLE_TO_UPDATE_PASSWORD_LOG, ServiceResponseCode.UNABLE_TO_UPDATE_PASSWORD_LOG_MSG));
                    }
                }
            } else {
                // log the new password
                if (null == passwordValidationService.savePasswordLog(encryptedPassword, user))
                {
                    LOG.error("Unable to save password log");
                    return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.UNABLE_TO_UPDATE_PASSWORD_LOG, ServiceResponseCode.UNABLE_TO_UPDATE_PASSWORD_LOG_MSG));
                }
            }

            user.setPassword(encryptedPassword);
            if (user.isChange_password())
                user.setChange_password(false);
            user.setPasswordUpdatedAt(new Date());
            try {
                if (null == userService.save(user))
                    return ResponseEntity.badRequest().body(new ServiceResponse(ServiceResponseCode.UNABLE_TO_UPDATE_USER_PASSWORD, ServiceResponseCode.UNABLE_TO_UPDATE_USER_PASSWORD_MSG));
            } catch (CMMSException e) {
                e.printStackTrace();
                return ResponseEntity.status(Integer.valueOf(e.getCode())).body(e.getMessage());
            }

            return ResponseEntity.ok().body(new ServiceResponse(ServiceResponseCode.VALID, ServiceResponseCode.PASSWORD_UPDATED));
        } else {
            return ResponseEntity.badRequest().body(new ServiceResponse( "00","Password empty, please chose a new password"));
        }

    }

    //
//    /@PutMapping("/enroller")
//    public ResponseEntity updateDesktopPassword(@Valid @RequestBody UpdatePasswordRequest request){
//        // check password validity
//        PasswordPolicyResponse passwordPolicyResponse = passwordValidationService.isValid(request.getUsername(), request.getNewPassword());
//        if (null != passwordPolicyResponse && null != passwordPolicyResponse.getResponseCode() && passwordPolicyResponse.getResponseCode().equals("00")) {
//            List<UserType> userTypes = new ArrayList<>();
//            userTypes.add(UserType.BANK);
//            userTypes.add(UserType.BANK);
//
//            User user = userService.getByUsernameAndUserType(request.getUsername(), userTypes);
//            if (user == null){
//                return ResponseEntity.badRequest().body(new ErrorDetails("Invalid credential"));
//            }
//
//            if (!request.getPassword().equals(user.getPassword())){
//                return ResponseEntity.badRequest().body(new ErrorDetails("Your old password is not correct"));
//            }
//
//            // update the last six used passwords
//            String encryptedPassword = EncyptionUtil.doSHA512Encryption(request.getNewPassword(), salt);
//            List<PasswordLog> passwordLogs = passwordValidationService.getUserPasswordLogs(user);
//            if (passwordLogs.size() >= 6) {
//                // get the oldest password
//                PasswordLog oldestPasswordLog = passwordLogs.stream().max(Comparator.comparing(PasswordLog::getCreatedAt)).orElse(null);
//                if (null == oldestPasswordLog) {
//                    LOG.trace("Unable to locate the oldest password");
//                    return ResponseEntity.badRequest().body(new ErrorDetails("Something went wrong while trying to log user's password, please try again."));
//                } else {
//                    // update password log
//                    PasswordLog updatedPasswordLog = passwordValidationService.updatePasswordLog(oldestPasswordLog, encryptedPassword);
//                    if (null == updatedPasswordLog) {
//                        LOG.trace("Unable to update the oldest password");
//                        return ResponseEntity.badRequest().body(new ErrorDetails("Something went wrong while trying to log user's password, please try again."));
//                    }
//                }
//            } else {
//                // log the new password
//                if (null == passwordValidationService.savePasswordLog(encryptedPassword, user))
//                    return ResponseEntity.badRequest().body(new ErrorDetails("Something went wrong while trying to log user's password, please try again."));
//            }
//
//            user.setPassword(encryptedPassword);
//            if (user.isChange_password())
//               user.setChange_password(false);
//            user.setPasswordUpdatedAt(new Date());
//            if (null == userService.save(user))
//                 return ResponseEntity.badRequest().body(new ErrorDetails("Something went wrong while trying to update user details, please try again."));
//            return ResponseEntity.ok("ok");
//        } else {
//            return ResponseEntity.badRequest().body(new ErrorDetails(null != passwordPolicyResponse && null != passwordPolicyResponse.getResponseMessage() ? passwordPolicyResponse.getResponseMessage() : "Something went wrong when trying to validate user's password, please try again"));
//        }
//
//    }


}
