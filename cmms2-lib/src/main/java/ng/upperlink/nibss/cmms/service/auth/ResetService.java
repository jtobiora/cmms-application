package ng.upperlink.nibss.cmms.service.auth;

import ng.upperlink.nibss.cmms.dto.Response;
import ng.upperlink.nibss.cmms.dto.ValidationResponseCode;
import ng.upperlink.nibss.cmms.util.PasswordUtil;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by stanlee on 29/03/2018.
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class ResetService {

    //private InstitutionCredentialsRepo institutionCredentialsRepo;

    @Value("${email_from}")
    private String fromEmail;

    private String password;

    private SmtpMailSender smtpMailSender;


    private static Logger LOG = LoggerFactory.getLogger(ResetService.class);



    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }


    /**
     * @param organisationCodeBase64 this is a code given to the institution, it is only known by nibss and the institution
     * @param serviceName This is name of the service that organisation is trying to use.
     * @return
     */
    public ResponseEntity reset(String organisationCodeBase64, String serviceName){

        LOG.info("Base64 of the OrganisationCOde sent is {}", organisationCodeBase64);

        MultiValueMap<String, String> headerValues = new LinkedMultiValueMap<>();

        //decode the auth.
        String organisationCode;
        try {
            organisationCode = new String(Base64.getDecoder().decode(organisationCodeBase64));
        } catch (Exception e) {
            LOG.error("Unable to decode Organisation code => {}", organisationCodeBase64, e);
            headerValues.add("ResponseCode", Response.INVALID_DATA_PROVIDED.getCode());
            return new ResponseEntity(headerValues, HttpStatus.BAD_REQUEST);
        }

        LOG.info("The decoded organisationCode is {}", organisationCode);

        //validate
        ResponseEntity responseEntity = validateRequest(organisationCode);
        if (responseEntity != null){
            return responseEntity;
        }

        //InstitutionCredentials credentials = authenticateAndUpdateCredentials(organisationCode, serviceName);

       /* //response back with a message
        if (credentials == null){
            LOG.info("response is {} ---> INVALID_LOGIN_CREDENTIALS",Response.INVALID_LOGIN_CREDENTIALS.getCode());
            headerValues.add("ResponseCode", Response.INVALID_LOGIN_CREDENTIALS.getCode());
            return new ResponseEntity(headerValues, HttpStatus.BAD_REQUEST);
        }*/


       /* headerValues.add("ResponseCode", Response.OK.getCode());
        headerValues.add("APIKey", credentials.getAesKey());
        headerValues.add("IVKey", credentials.getIvSpec());
        headerValues.add("Password", password);

        LOG.info("response is {} ---> OK",Response.OK.getCode());
        LOG.info("APIKey is {} ---> OK",credentials.getAesKey());
        LOG.info("IVKey is {} ---> OK",credentials.getIvSpec());*/

        return new ResponseEntity(headerValues, HttpStatus.OK);
    }

    private ResponseEntity validateRequest(String organisationCode){

        if (organisationCode == null || organisationCode.isEmpty() || "".equalsIgnoreCase(organisationCode)){
            LOG.info("response is {} ---> NOT_FOUND",Response.NOT_FOUND.getCode());
            return ResponseEntity.badRequest().body(new ValidationResponseCode(Response.NOT_FOUND.getCode()));
        }

        return null;
    }

    /*private InstitutionCredentials authenticateAndUpdateCredentials(String organisationCode, String serviceName){

        InstitutionCredentials institutionCredentials = null;

        //authenticate
        Optional<InstitutionCredentials> credentials = institutionCredentialsRepo.getInstitutionCredentialsByInstitutionsAndService(organisationCode, serviceName);
        if (!credentials.isPresent()){

            //instead of returning null, confirm that the institution exist on the institutionservice table and it is enabled
            Optional<InstitutionService> institutionAndService = institutionServiceRepo.getByInstitutionAndServiceAndEnabled(organisationCode, serviceName);
            if (!institutionAndService.isPresent()){
               //truly invalid organisationCode
                return null;
            }

            InstitutionService institutionService = institutionAndService.get();

            //insert into the credentials table.
          *//*  institutionCredentials = new InstitutionCredentials();
            institutionCredentials.setAesKey("");
            institutionCredentials.setEnabled(100);
            institutionCredentials.setInstitutions(institutionService.getInstitution());
            institutionCredentials.setIvSpec("");
            institutionCredentials.setPassword("");
            institutionCredentials.setService(institutionService.getService() != null ? institutionService.getService().getId() : null);
            institutionCredentials.setUpdatedAt(new Date());*//*
        }

        if (institutionCredentials == null){
            institutionCredentials = credentials.get();
        }


        String aesKey;
        String ivKey;
        try {
            aesKey = EncyptionUtil.generateKey();
            ivKey = EncyptionUtil.generateKey();
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Unable to generateAuthRequest aesKey and ivKey",e);
            return null;
        }

        institutionCredentials.setAesKey(aesKey);
        institutionCredentials.setIvSpec(ivKey);

        //generateAuthRequest new password
        String newPassword = generateNewPassword();
        this.password = newPassword;

        //hash the password
        //save the password
        institutionCredentials.setPassword(PasswordUtil.hashPassword(newPassword, institutionCredentials.getInstitutions().getCode(),institutionCredentials.getIvSpec()));
        institutionCredentialsRepo.save(institutionCredentials);

        //send mail
        //send a mail containing all the information according to the doc.
        sendMail(institutionCredentials, newPassword);

        return institutionCredentials;
    }*/

    private String generateNewPassword(){
        return PasswordUtil.generateRandom(16);
    }

    /*private void sendMail(InstitutionCredentials credentials, String newPassword){

        String details = generateDetails(credentials, newPassword);
        String[] emailAddress = {credentials.getInstitutions().getEmailAddress()};

        //Send the mail
        smtpMailSender.sendMail(fromEmail, emailAddress, "BVN Agency Banking API Credentials",
                "BVN Agency Banking API Credentials", "Your recently called for reset of your BVN Agency Banking API Credentials. Kindly find below your new credentials:", details);
    }*/

    /*private String generateDetails(InstitutionCredentials credentials, String newPassword){

        String details = "";
        details += "<strong>Organization Name :</strong> " + credentials.getInstitutions().getName() + "<br/>";
        details += "<strong>Organization Code :</strong> " + credentials.getInstitutions().getCode() + "<br/>";
        details += "<strong>IV :</strong> " + credentials.getIvSpec() + "<br/>";
        details += "<strong>AES Key :</strong> " + credentials.getAesKey() + "<br/>";
        details += "<strong>Password :</strong> " + newPassword + "<br/>";
        return details;
    }*/

   /* public Institutions insertInstitutionDetails(AgentManagerInstitution agentManagerInstitution){

        //confirm that the institutions exists to update or insert
        Institutions institutions = institutionRepo.getByCode(agentManagerInstitution.getCode());
        if (institutions == null){
            institutions = new Institutions();
            institutions.setEnabled(100);
        }
        institutions.setCode(agentManagerInstitution.getCode());
        institutions.setEmailAddress(agentManagerInstitution.getEmailAddress());
        institutions.setName(agentManagerInstitution.getName());
        return institutionRepo.save(institutions);
    }*/

    /*public com.upl.nibss.bvn.model.security.Service getServiceName(String name){
        com.upl.nibss.bvn.model.security.Service service = serviceRepo.getByName(name);
        if (service == null){
            service = new com.upl.nibss.bvn.model.security.Service();
            service.setName(name);
            return serviceRepo.save(service);
        }

        return service;
    }
*/
    /*public InstitutionService insertInstitutionService(Institutions institutions, com.upl.nibss.bvn.model.security.Service service){

        //confirm that the institutionService exists to update or insert
        InstitutionService institutionService = new InstitutionService();
        Optional<InstitutionService> institutionServiceResult = institutionServiceRepo.getByInstitutionAndService(institutions.getCode(), service.getName());
        if (institutionServiceResult.isPresent()){
            institutionService = institutionServiceResult.get();
        }

        institutionService.setInstitution(institutions);
        institutionService.setService(service);
        return institutionServiceRepo.save(institutionService);
    }*/

    /*public void toggleInstitutionCredential(AgentManagerInstitution agentManagerInstitution){

        //if the agent manager is disabled then disable else set to enabled
        if (agentManagerInstitution.isActivated()){
            institutionCredentialsRepo.toggleBiller(100, agentManagerInstitution.getCode());
            institutionRepo.toggleBiller(100,agentManagerInstitution.getCode());
        }else {
            institutionCredentialsRepo.toggleBiller(200, agentManagerInstitution.getCode());
            institutionRepo.toggleBiller(200,agentManagerInstitution.getCode());
        }
    }
*/
}
