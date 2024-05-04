package ng.upperlink.nibss.cmms.service.emandate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.emandates.AuthParam;
import ng.upperlink.nibss.cmms.dto.emandates.ConfigurationRequest;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.makerchecker.*;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.emandate.EmandateConfig;
import ng.upperlink.nibss.cmms.repo.emandate.EmandateConfigRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import ng.upperlink.nibss.cmms.service.makerchecker.UserAuthorizationService;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigureEmandateService {

    @Value("${encryption.salt}")
    private String salt;

    @Value("${nibss-identity-key}")
    private String nibssId;

    private SmtpMailSender smtpMailSender;

    @Value("${email_from}")
    private String fromEmail;

    private static Logger logger = LoggerFactory.getLogger(ConfigureEmandateService.class);
    private BillerService billerService;
    private BankService bankService;
    private UserService userService;
    private OtherAuthorizationService authorizationService;
    private EmandateConfigRepo emandateConfigRepo;

    @Autowired
    public void setAuthorizationService(OtherAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }

    @Autowired
    public void setEmandateConfigRepo(EmandateConfigRepo emandateConfigRepo) {
        this.emandateConfigRepo = emandateConfigRepo;
    }

    @Autowired
    public void setBillerService(BillerService billerService) {
        this.billerService = billerService;
    }
    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public long getCountDomain(String domain)
    {
        return emandateConfigRepo.countByDomainName(domain);
    }
    public long getCountDomain(String domain, Long id)
    {
        return emandateConfigRepo.countByDomainName(domain,id);
    }
    public long getCountUsername(String username)
    {
        return emandateConfigRepo.countByUsername(username);
    }
    public long getCountUsername(String username, Long id)
    {
        return emandateConfigRepo.countByUsername(username,id);
    }

    //    public EmandateConfig getByid(Long id)
//    {
//        return emandateConfigRepo.getById(id);
//    }
//    public EmandateConfig getByUsername(String username)
//    {
//        return emandateConfigRepo.getByUserName(username);
//    }
//    public EmandateConfig getByDomain(String domain)
//    {
//        return emandateConfigRepo.getByDomainName(domain);
//    }

    public void validate(ConfigurationRequest request, boolean isUpdate) throws CMMSException {

        long username = 0;
        long domain = 0;

        if (isUpdate){
            if (request.getUsername() == null){
                throw new CMMSException("Username is not provided","400","400");
            }
            if (request.getDomainName() == null){
                throw new CMMSException("Domain name is not provided","400","400");
            }
            username = getCountUsername(request.getUsername(),request.getId());
            domain = getCountDomain(request.getDomainName(),request.getId());
        }else {
            username = getCountUsername(request.getUsername());
            domain = getCountDomain(request.getDomainName());

        }
        if (username > 0) {
            throw new CMMSException("Username "+ request.getUsername() + "' already exist","400","400");

        }
        if (domain > 0) {
            throw new CMMSException("Domain "+ request.getDomainName() + "' already exist","400","400");

        }
    }
    public EmandateConfig save(EmandateConfig emandateConfig){
        return emandateConfigRepo.save(emandateConfig);
    }
    public EmandateConfig getOne(Long id)
    {
        return emandateConfigRepo.getOne(id);
    }
    @Transactional
    public AuthorizationTable setUp (ConfigurationRequest request, UserDetail userDetail,
                                          AuthorizationAction action, InitiatorActions initiatorActions, boolean isUpdate) throws CMMSException {

        EmandateConfig existingRecord = null;
        EmandateConfig newRecord = new EmandateConfig();
        validate(request,isUpdate);
        AuthorizationTable object = null;
        if (!isUpdate)
        {
            object= authorizationService.getById(request.getOwnerId());
            if (object == null)
                throw new CMMSException(request.getEntityType()+" to be configured is not found,please try again","404","404");
        }
        Bank objBank =null;
        Biller objBiller =null;
//        try {
                User operatorUser = userService.get(userDetail.getUserId());
                if (operatorUser == null){
                    throw new CMMSException("Please login and try again","404","404");
                }
                switch (request.getEntityType())
                {
                    case BANK:
                        bankService.authenticate(operatorUser,action,initiatorActions);
                        if (!isUpdate)
                        {
                            objBank = (Bank)object;
                            if (objBank.getEmandateConfig() !=null)
                            throw new CMMSException("Cannot save another configuration against "+objBank.getName(),"400","400");
                        }
                        break;
                    case BILLER:
                        billerService.authenticate(operatorUser,action,initiatorActions);
                        if (!isUpdate)
                        {
                            objBiller = (Biller)object;
                            if (objBiller.getEmandateConfig() !=null)
                            throw new CMMSException("Cannot save another configuration against "+objBiller.getName(),"400","400");
                        }
                        break;
                }
                if (isUpdate)
                {
                    if (request.getId() == 0 || request.getId() ==null){
                        throw new CMMSException("Emandate config Id id is not provided","400","400");
                    }

                    existingRecord = emandateConfigRepo.getById(request.getId());
                    if(existingRecord == null)
                    {
                        throw new CMMSException(request.getEntityType()+" configuration to be updated not found","404","404");
                    }
                    existingRecord = generate(newRecord,existingRecord,request,operatorUser,true);
                    return save(existingRecord);
                }
                else {

                    newRecord = generate(newRecord,null, request, operatorUser, false);
                    if (newRecord ==null)
                        throw new CMMSException("Configuration failed","500","500");

                    newRecord = save(newRecord);
                    switch (request.getEntityType())
                    {
                        case BANK: objBank.setEmandateConfig(newRecord);
                        bankService.save(objBank);
                        break;
                        case BILLER:objBiller.setEmandateConfig(newRecord);
                    }
                    return newRecord;
                }
//        }catch (Exception e){
//            e.printStackTrace();
//            logger.error(e.toString());
//            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
//        }
    }
    public EmandateConfig generate(EmandateConfig newRecord,EmandateConfig existingRecord,ConfigurationRequest request,User operator,boolean isUpdate) throws CMMSException {
        String jsonData =null;
        try {
            generateUpdate(newRecord,request);
           if (isUpdate)
           {
                newRecord.setUpdatedBy(userService.setUser(operator));
//                newRecord.setUpdatedAt(new Date());
                jsonData = JsonBuilder.generateJson(newRecord);
                if (jsonData ==null)
                    throw new CMMSException("Could not update the record","500","500");
               return (EmandateConfig) authorizationService.actions(jsonData,existingRecord,operator,null, InitiatorActions.UPDATE,null, EntityType.EMANDATE);


            }else
           {
//               newRecord.setCreatedAt(new Date());
               newRecord.setCreatedBy(userService.setUser(operator));
               String clientPassKey = userService.generatePassword();
               if (clientPassKey ==null)
               {
                   logger.error("Could not generatte Client pass key");
                   return null;
               }
               newRecord.setClientPassKey(clientPassKey);
               return (EmandateConfig) authorizationService.actions(null,newRecord,operator,null, InitiatorActions.CREATE,null, EntityType.EMANDATE);
           }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.error(e.toString());
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
            }
        }
    public EmandateConfig generateApproved(EmandateConfig existingRecord,EmandateConfig fromJson,User operator){

//             existingRecord.setClientPassKey(fromJson.getClientPassKey());
             if (StringUtils.isNotEmpty(fromJson.getDomainName()))
                 existingRecord.setDomainName(fromJson.getDomainName());
             if (StringUtils.isNotEmpty(fromJson.getUsername()))
                 existingRecord.setUsername(fromJson.getUsername());
             if (StringUtils.isNotEmpty(fromJson.getNotificationUrl()))
                 existingRecord.setNotificationUrl(fromJson.getNotificationUrl());
             if (StringUtils.isNotEmpty(fromJson.getPassword()))
                 existingRecord.setPassword(fromJson.getPassword());
             existingRecord.setUpdatedBy(fromJson.getUpdatedBy());
             existingRecord.setApprovedAt(new Date());
             existingRecord.setApprovedBy(userService.setUser(operator));
        return existingRecord;
    }

    public void generatePassword(EmandateConfig existingRecord,UserType userType) throws CMMSException {
        Bank bank = null;
        Biller biller =null;
        String apiKey = null;
        switch (userType)
        {
            case BANK:
                bank = bankService.getBankByUsername(existingRecord.getUsername());
                if (bank == null)
                {
                    throw new CMMSException("No Emandate configuration setting found on this bank","404","404");
                }
                apiKey = bank.getApiKey();
                break;
            case BILLER:
                biller = billerService.getBillerByUsername(existingRecord.getUsername());
                if (bank == null)
                {
                    throw new CMMSException("No Emandate configuration setting found on this biller","404","404");
                }
                apiKey = biller.getApiKey();
                break;
                default:
                    throw new CMMSException("Entity type "+userType.getValue()+" not allowed","400","400");

        }
        String password = userService.generatePassword();
        if (password ==null)
        {
            logger.error("Could not generate Client password ");
        }
        logger.info("Generated password : "+password);
        if (!password.isEmpty())
            existingRecord.setPassword(EncyptionUtil.doSHA512Encryption(password, salt));
        existingRecord = save(existingRecord);
        if (existingRecord !=null)
        {
            String[] emails = {existingRecord.getCreatedBy().getEmailAddress(),existingRecord.getApprovedBy().getEmailAddress()};
            this.sendRecoveryEmail(emails,new AuthParam(existingRecord.getUsername(),password,apiKey),null,false);
        }
        //TODO Send mail to the biller or bank containing their login details
    }

    public EmandateConfig generateUpdate(EmandateConfig newRecord ,ConfigurationRequest request){
        newRecord.setUsername(request.getUsername());
        newRecord.setDomainName(request.getDomainName());
        newRecord.setNotificationUrl(request.getNotificationUlr());
        return newRecord;
    }

    public ResponseEntity<?> toggle(Long id, UserDetail userDetail,EntityType entityType) throws CMMSException {
        User operatorUser = userService.get(userDetail.getUserId());
        if (operatorUser == null){
            throw new CMMSException(Errors.UNKNOWN_USER.getValue(),"404","404");
        }
        switch (entityType)
        {
            case BANK:
                bankService.authenticate(operatorUser,null,InitiatorActions.TOGGLE);
                break;
            case BILLER:
                billerService.authenticate(operatorUser,null,InitiatorActions.TOGGLE);
                break;
        }
        EmandateConfig emandateConfig = emandateConfigRepo.getById(id);
        if (emandateConfig == null)
            throw new CMMSException("Toggling failed, E-mandate configuration not found","404","404");

        emandateConfig = (EmandateConfig) authorizationService.actions(null,emandateConfig,operatorUser,null,InitiatorActions.TOGGLE,null,EntityType.EMANDATE);

        return ResponseEntity.ok(save(emandateConfig));
    }

    public Page<Biller> selectViewBiller(UserDetail userDetail, ViewAction viewAction, Pageable pageable) throws CMMSException {
                return billerService.selectViewConfig(userDetail,viewAction, pageable);
    }
    public Page<Bank> selectViewBank(UserDetail userDetail, ViewAction viewAction, Pageable pageable) throws CMMSException {
                return bankService.selectView(viewAction, pageable);
    }
    public EmandateConfig previewUpdate(Long id) throws CMMSException
    {
        EmandateConfig emandateConfig = emandateConfigRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        if (emandateConfig == null)
            throw new CMMSException("No E mandate configuration to preview ","400","400");
        if (StringUtils.isEmpty(emandateConfig.getJsonData()))
        {
            throw new CMMSException("No content to preview ","400","400");
//            return null;
        }
        String jsonData = emandateConfig.getJsonData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            EmandateConfig jsonUser = mapper.readValue(jsonData, EmandateConfig.class);
            return jsonUser;
        } catch (IOException e) {
            logger.error(e.toString());
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500",EmandateResponseCode.UNKNOWN.getCode());
        }
    }

    public void authenticate(User operator, AuthorizationAction action,InitiatorActions initiatorActions,EmandateConfig emandateConfig) throws CMMSException {
        if(operator.getUserType().equals(UserType.NIBSS))
        {
            bankService.authenticate(operator,action,initiatorActions);
                        billerService.authenticate(operator,action,initiatorActions);
        }else
        {
            billerService.authenticate(operator,action,initiatorActions);
        }
    }

    public void sendRecoveryEmail(String[] email, AuthParam authParam, String passwordOrCode, boolean isRecovery) {
        String details;
        String subject;
        String subtitle;
        if (!isRecovery)
        {
            details = "Username: "+authParam.getUsername() +
                    "\n Password: "+authParam.getPassword()+
                    "\n API Key: "+authParam.getApiKey() +
                    "\n Please note that this credential is unique and should be secured. " +
                    "\n Do not disclose this mail to unauthorized person";
            subject = "Electronic mandate Credential";
            subtitle ="Kindly find below the Electronic mandate credential.";
        }
        else
        {
            details = "Password Recovery Code: "+passwordOrCode+
            "\n Please note that this code will expire in 24 hours.";
            subject ="Password Reset";
            subtitle ="Kindly copy and paste the code below on the portal to continue. ";
        }

        //Send the mail
        smtpMailSender.sendMail(fromEmail, email, subject,
                "Password Recovery", subtitle, details);
    }

}
