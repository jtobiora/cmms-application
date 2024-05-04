package ng.upperlink.nibss.cmms.service.emandate;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.ServiceResponse;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.emandates.AuthParam;
import ng.upperlink.nibss.cmms.dto.emandates.EmandatePasswordRequest;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.ServiceResponseCode;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.emandate.EmandateConfig;
import ng.upperlink.nibss.cmms.model.emandate.EmandatePasswordRecoveryCode;
import ng.upperlink.nibss.cmms.repo.emandate.EmandatePasswordRecoveryRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmandatePasswordService {
    @Value("${encryption.salt}")
    private String salt;

    private SmtpMailSender smtpMailSender;

    @Value("${email_from}")
    private String fromEmail;

    private UserService userService;
    private EmandatePasswordRecoveryRepo emandatePasswordRecoveryRepo;
    private ConfigureEmandateService configureEmandateService;
    private OtherAuthorizationService otherAuthorizationService;

    @Autowired
    public void setOtherAuthorizationService(OtherAuthorizationService otherAuthorizationService) {
        this.otherAuthorizationService = otherAuthorizationService;
    }

    @Autowired
    public void setConfigureEmandateService(ConfigureEmandateService configureEmandateService) {
        this.configureEmandateService = configureEmandateService;
    }

    @Autowired
    public void setEmandatePasswordRecoveryRepo(EmandatePasswordRecoveryRepo emandatePasswordRecoveryRepo) {
        this.emandatePasswordRecoveryRepo = emandatePasswordRecoveryRepo;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    public String generateRecoveryCode(UserDetail userDetail) throws CMMSException {
        User operator = userService.get(userDetail.getUserId());
        EmandatePasswordRecoveryCode emandatePasswordRecoveryCode =null;
        if (operator ==null)
        {
            throw new CMMSException("User is not found, please login and try again","404","404");
        }
        String recoveryCode = EncyptionUtil.generateString(20, true, true);
        switch (operator.getUserType())
        {
            case BANK:
                Bank bank = ((BankUser)operator).getUserBank();
                emandatePasswordRecoveryCode = save(recoveryCode,new Bank(bank.getId(),bank.getCode(),bank.getName()));
                authenticate(bank.getId(),operator,InitiatorActions.UPDATE, null);
                break;
            case BILLER:
                Biller biller = ((BillerUser)operator).getBiller();
                emandatePasswordRecoveryCode = this.save(recoveryCode,new Biller(biller.getId(),biller.getName(),biller.getRcNumber()));
                authenticate(biller.getId(),operator,InitiatorActions.UPDATE, null);
                break;
        }
        if (emandatePasswordRecoveryCode == null)
        {
            throw new CMMSException(ServiceResponseCode.PASSWORD_RECOVERY_CODE_ERROR_MSG,"500","500");
        }
        String[] emails = {operator.getEmailAddress()};

        configureEmandateService.sendRecoveryEmail(emails,null, recoveryCode,true);
        return "Recovery password has been sent to your registered mail";
    }

    public boolean confirmRecoveryCode(UserDetail userDetail,String recoveryCode) throws CMMSException {
        User operator = userService.get(userDetail.getUserId());
        EmandatePasswordRecoveryCode emandatePasswordRecoveryCode =null;
        if (operator ==null)
        {
            throw new CMMSException("User is not found, please login and try again","404","404");
        }
            authenticate(null,operator,InitiatorActions.UPDATE,null);
        emandatePasswordRecoveryCode = findRecoveryCode(recoveryCode);
        if (emandatePasswordRecoveryCode == null)
        {
            throw new CMMSException(ServiceResponseCode.RECOVERY_CODE_DOES_NOT_EXIST_MSG,"404","404");
        }

        // check if this recovery code has been used before
        if (emandatePasswordRecoveryCode.isStatus()) {
            throw new CMMSException(ServiceResponseCode.RECOVERY_CODE_USED_MSG,"400","400");
        }
        if (checkValidityPeriod(emandatePasswordRecoveryCode.getCreatedAt())>=1)
        {
            throw new CMMSException(ServiceResponseCode.RECOVERY_CODE_EXPIRED_MSG,"400","400");
        }
        updateRecoveryCode(emandatePasswordRecoveryCode);
    return true;
    }
    private void authenticate(Long objectId,User operator, InitiatorActions initiatorActions, AuthorizationAction action) throws CMMSException {

            Collection<Role> roles;
            roles = operator.getRoles();
            if (roles == null) {
                throw new CMMSException(Errors.NOT_PERMITTED.getValue(), "401", "401");
            }
            Role operatorRole = roles.stream().findAny().get();
            switch (operator.getUserType())
            {
                case BANK:
                        if (initiatorActions !=null && !operatorRole.getName().equals(RoleName.BANK_ADMIN_INITIATOR))
                    {
                        throw new CMMSException(Errors.NOT_PERMITTED_TO_UPDATE_PSW.getValue().replace("{}",RoleName.BANK_ADMIN_INITIATOR.getValue()),"401","401");
                    }
                    if (action !=null && !operatorRole.getName().equals(RoleName.BANK_ADMIN_AUTHORIZER))
                    {
                        throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE_PSW.getValue().replace("{}",RoleName.BANK_ADMIN_AUTHORIZER.getValue()),"401","401");
                    }
                    if (objectId !=null) {
                        if (((BankUser) operator).getUserBank().getId() != objectId) {
                            throw new CMMSException("This is not your bank, action not permitted", "401", "401");
                        }
                    }
                    break;
                case BILLER:
                    if (initiatorActions !=null && !operatorRole.getName().equals(RoleName.BILLER_ADMIN_INITIATOR))
                    {
                        throw new CMMSException(Errors.NOT_PERMITTED_TO_UPDATE_PSW.getValue().replace("{}",RoleName.BILLER_ADMIN_INITIATOR.getValue()),"401","401");
                    }
                    if (action !=null && !operatorRole.getName().equals(RoleName.BILLER_ADMIN_AUTHORIZER))
                    {
                        throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE_PSW.getValue().replace("{}",RoleName.BILLER_ADMIN_AUTHORIZER.getValue()),"401","401");
                    }
                    if (objectId !=null) {
                        if (((BillerUser) operator).getBiller().getId() != objectId) {
                            throw new CMMSException("This is not your biller, action not permitted", "401", "401");
                        }
                    }
                    break;
            }
    }

    private EmandatePasswordRecoveryCode save(String recoveryCode, AuthorizationTable object) {
        try {
            EmandatePasswordRecoveryCode passwordRecoveryCode = new EmandatePasswordRecoveryCode();
            passwordRecoveryCode.setRecoveryCode(recoveryCode);
            passwordRecoveryCode.setObject(object);
            return emandatePasswordRecoveryRepo.saveAndFlush(passwordRecoveryCode);
        } catch (Exception e) {
            log.error("Unable to save password recovery code", e);
        }
        return null;
    }

    private EmandatePasswordRecoveryCode findRecoveryCode(String recoveryCode) throws CMMSException {
        try {
            return emandatePasswordRecoveryRepo.findRecoveryCode(recoveryCode);
        } catch (Exception e) {
            log.error("Unable to find recovery code {}", recoveryCode, e);
            throw new CMMSException("Recovery code: "+recoveryCode+" does not exist","404","404");
        }
    }

    private long checkValidityPeriod(Date createdDate) {
        long diffInMillies = Math.abs(new Date().getTime() - createdDate.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private void updateRecoveryCode(EmandatePasswordRecoveryCode passwordRecoveryCode) {
        try {
            passwordRecoveryCode.setUpdatedAt(new Date());
            passwordRecoveryCode.setStatus(true);
            emandatePasswordRecoveryRepo.saveAndFlush(passwordRecoveryCode);
        } catch (Exception e) {
            log.error("Unable to update password recovery code {}", passwordRecoveryCode, e);
        }
    }
    public void updatePassword (EmandatePasswordRequest request, UserDetail userDetail) throws CMMSException {
        User operator = userService.get(userDetail.getUserId());
        if (operator == null)
        {
            throw new CMMSException("Unknown user,try again","401","401");
        }
            authenticate(null,operator,InitiatorActions.UPDATE,null);
       try {
           EmandateConfig emandateConfig = configureEmandateService.getOne(request.getObjectId());
           if (emandateConfig ==null)
           {
               throw new CMMSException("No configuration found for the mandate","404","404");
           }
           EmandateConfig newRecord = new EmandateConfig();
           newRecord.setPassword(EncyptionUtil.doSHA512Encryption(request.getPassword(), salt));
           String jsonData = JsonBuilder.generateJson(newRecord);
           emandateConfig = (EmandateConfig) otherAuthorizationService.actions(jsonData,emandateConfig,operator,null,InitiatorActions.UPDATE,null, EntityType.EMANDATE);
           configureEmandateService.save(emandateConfig);
           /*TODO Attach the email of  the authorizer*/
           String [] email = {};
           this.sendPasswordUpdateEmail(email,request.getPassword());
       }catch (Exception e)
       {
           log.error("Save error {}",e);
       }
    }
    private void sendPasswordUpdateEmail(String[] email, String passwordOrCode) {
        String details;
        String subject;
        String subtitle;
            details = "New password: "+passwordOrCode+
                    "\n Please note that this password is for e-mandate configuration.";
            subject ="Password Update";
            subtitle ="Kindly review and authorize the password update  ";

        //Send the mail
        smtpMailSender.sendMail(fromEmail, email, subject,
                "Password Reset", subtitle, details);
    }
}
