package ng.upperlink.nibss.cmms.service.emandate;

import com.fasterxml.jackson.core.JsonProcessingException;
import ng.upperlink.nibss.cmms.dto.emandates.AuthParam;
import ng.upperlink.nibss.cmms.dto.emandates.EMandateRequestBody;
import ng.upperlink.nibss.cmms.dto.emandates.EmandateRequest;
import ng.upperlink.nibss.cmms.dto.emandates.StatusRequest;
import ng.upperlink.nibss.cmms.dto.emandates.response.EmandateResponse;
import ng.upperlink.nibss.cmms.dto.emandates.response.StatusResponse;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.emandate.McashResponseCode;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.Product;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.model.mandate.MandateStatus;
import ng.upperlink.nibss.cmms.repo.MandateRepo;
import ng.upperlink.nibss.cmms.service.QueueService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateStatusService;
import ng.upperlink.nibss.cmms.util.CommonUtils;
import ng.upperlink.nibss.cmms.util.DateUtils;
import ng.upperlink.nibss.cmms.util.emandate.EMandateValidator;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class EmandateBaseService {

    @Value("${encryption.salt}")
    private String salt;
    private static final Logger logger = LoggerFactory.getLogger(EmandateBaseService.class);
    private BankService bankService;
    private BillerService billerService;
    private EMandateValidator eMandateValidator;
    private BankEmandateService bankEmandateService;
    private BillerEmandateService billerEmandateService;
    private MandateRepo mandateRepo;
    private MandateStatusService mandateStatusService;
    private QueueService queueService;

    @Autowired
    public void setQueueService(QueueService queueService) {
        this.queueService = queueService;
    }

    @Autowired
    public void setMandateStatusService(MandateStatusService mandateStatusService){
        this.mandateStatusService = mandateStatusService;
    }
    @Autowired
    public void setBankService(BankService bankService)
    {
        this.bankService = bankService;
    }
    @Autowired
    public void setBillerService(BillerService billerService)
    {
        this.billerService = billerService;
    }
    @Autowired
    public void seteMandateValidator(EMandateValidator eMandateValidator) {
        this.eMandateValidator = eMandateValidator;
    }
    @Autowired
    public void setBankEmandateService(BankEmandateService bankEmandateService) {
        this.bankEmandateService = bankEmandateService;
    }
    @Autowired
    public void setBillerEmandateService(BillerEmandateService billerEmandateService) {
        this.billerEmandateService = billerEmandateService;
    }
    @Autowired
    public void setMandateRepo(MandateRepo mandateRepo) {
        this.mandateRepo = mandateRepo;
    }

    public Bank authenticateBank(AuthParam auth) throws CMMSException {
            if (StringUtils.isEmpty(auth.getUsername()))
            {
                logger.info("Username cannot be null");
                throw new CMMSException("pass code cannot be null","400",EmandateResponseCode.EMPTY_CREDENTIAL.getCode());

            }
                Bank bank = bankService.getBankByUsername(auth.getUsername());
            if (bank ==null)
            {
                logger.error("Client is not found : Invalid credentials ");
                throw new CMMSException("Client is not found : Invalid credentials ","400",EmandateResponseCode.BANK_NOT_FOUND.getCode());
            }
            if (StringUtils.isEmpty(auth.getPassword()))
            {
                logger.info("pass code cannot be null");
                throw new CMMSException("pass code cannot be null","400",EmandateResponseCode.EMPTY_CREDENTIAL.getCode());
            }
            if (!EncyptionUtil.doSHA512Encryption(auth.getPassword(),salt).equals(bank.getEmandateConfig().getPassword()))
            {
                logger.info("Bank Password does not match");
                throw new CMMSException("Bank Password does not match","400",EmandateResponseCode.INVALID_CREDENTIALS.getCode());
            }
            if (!bank.getApiKey().equals(auth.getApiKey()))
            {
                logger.info("Bank Api does not match");
                throw new CMMSException("Bank Api does not match","400",EmandateResponseCode.INVALID_API_KEY.getCode());
            }
        if (!bank.getEmandateConfig().isActivated())
        {
            logger.error("E mandate is disabled");
            throw new CMMSException(EmandateResponseCode.EMANDAT_DISABLED.getValue().replace("{}",Optional.ofNullable(bank.getName()).orElse("this Bank")),"401",EmandateResponseCode.EMANDAT_DISABLED.getCode());
        }
        logger.info("Requeting Bank\n Name: "+Optional.ofNullable(bank.getName())+"\n"+"Username: "+Optional.ofNullable(bank.getEmandateConfig().getUsername()));


        return bank;
    }
    public Biller authenticateBiller(AuthParam auth) throws CMMSException {if (StringUtils.isEmpty(auth.getUsername()))
    {
        logger.info("Username cannot be null");
        throw new CMMSException("pass code cannot be null","400",EmandateResponseCode.EMPTY_CREDENTIAL.getCode());
    }
        Biller biller = billerService.getBillerByUsername(auth.getUsername());
        if (biller ==null)
        {
            logger.error("Biller is not found : Invalid credentials ");
            throw new CMMSException("Biller is not found : Invalid credentials ","400",EmandateResponseCode.BILLER_NOT_FOUND.getCode());
        }
        if (StringUtils.isEmpty(auth.getPassword()))
        {
            logger.info("pass code cannot be null");
            throw new CMMSException("pass code cannot be null","400",EmandateResponseCode.EMPTY_CREDENTIAL.getCode());
        }
        if (!EncyptionUtil.doSHA512Encryption(auth.getPassword(),salt).equals(biller.getEmandateConfig().getPassword()))
        {
            logger.info("Biller Password does not match");
            throw new CMMSException("Biller Password does not match","400",EmandateResponseCode.EMPTY_CREDENTIAL.getCode());
        }
        if (!biller.getApiKey().equals(auth.getApiKey()))
        {
            logger.info("Biller Api does not match");
            throw new CMMSException("Biller Api does not match","400",EmandateResponseCode.EMPTY_CREDENTIAL.getCode());
        }

        if (!biller.getEmandateConfig().isActivated())
        {
            logger.error("E mandate is disabled");
            throw new CMMSException(EmandateResponseCode.EMANDAT_DISABLED.getValue().replace("{}",Optional.ofNullable(biller.getName()).orElse("this Biller")),"401",EmandateResponseCode.EMANDAT_DISABLED.getCode());
        }
        logger.info("Requeting Biller\n Name: "+Optional.ofNullable(biller.getName())+"\n"+"Username: "+Optional.ofNullable(biller.getEmandateConfig().getUsername()));
        return biller;
    }
    public ResponseEntity<?> processIncomingRequest(EMandateRequestBody eMandateRequestBody, UserType userType){
        EmandateResponse emandateResponse = null;
        EmandateRequest emandateRequest = eMandateRequestBody.getEmandateRequest();
        String jSonResponse;
        try {
            if (emandateRequest ==null)
            {
                emandateResponse = generateEmandateResponse(EmandateResponseCode.EMPTY_MANDATE_REQUEST,null,"");
                return ResponseEntity.badRequest().body(emandateResponse);
            }
            switch (userType)
            {
                case BANK:
                    emandateResponse = eMandateValidator.validate(emandateRequest);
                    if (emandateResponse != null)
                    {
                        jSonResponse = JsonBuilder.generateJson(emandateResponse);
                        logger.info(jSonResponse);
                        return ResponseEntity.badRequest().body(emandateResponse);
                    }
                   authenticateBank(eMandateRequestBody.getAuth());

                    emandateResponse = bankEmandateService.processSaveUpdate(emandateRequest);
                    return returnEmandateResponse(emandateResponse);
                case BILLER:
                    emandateResponse = eMandateValidator.validate(emandateRequest);
                    if (emandateResponse != null)
                    {
                        logger.info(emandateResponse.toString());
                        return ResponseEntity.badRequest().body(emandateResponse);
                    }
                    authenticateBiller(eMandateRequestBody.getAuth());
                    emandateResponse = billerEmandateService.processMandate(emandateRequest);
                    return returnEmandateResponse(emandateResponse);

            }
            return ResponseEntity.status(201).body(emandateResponse);
        } catch (JsonProcessingException e) {
            logger.error("Json Error {}",e);
            e.printStackTrace();
           return returnEmandateResponse( new EmandateResponse(EmandateResponseCode.UNKNOWN.getCode(),null,EmandateResponseCode.UNKNOWN.getValue()));
        } catch (CMMSException e) {
            e.printStackTrace();
            return returnEmandateResponse(new EmandateResponse(e.getEmandateErrorCode(),null,e.getMessage()));
        }

    }

    private ResponseEntity<?> returnEmandateResponse(EmandateResponse emandateResponse) {
        if (!emandateResponse.getResponseCode().equals(EmandateResponseCode.CREATION_SUCCESSFUL.getCode()))
        {
            logger.info(emandateResponse.toString());
            return ResponseEntity.status(417).body(emandateResponse);
        }else
        {
            logger.info(emandateResponse.toString());
            return ResponseEntity.status(201).body(emandateResponse);
        }
    }

    public static EmandateResponse generateEmandateResponse(EmandateResponseCode emandateResponseCode,String mandateCode,String replace) {
        EmandateResponse emandateResponse;
        emandateResponse = new EmandateResponse(emandateResponseCode.getCode(),mandateCode,emandateResponseCode.getValue().replace("{}",replace));
        logger.info(emandateResponse.toString());
        return emandateResponse;
    }


    public static EmandateResponse generateMcashResponse(McashResponseCode mcashResponseCode, String mandateCode)  {
        EmandateResponse emandateResponse;
        emandateResponse = new EmandateResponse(Optional.ofNullable(mcashResponseCode.getCode()).orElse(EmandateResponseCode.UNKNOWN.getCode()),mandateCode,Optional.ofNullable(mcashResponseCode.getValue()).orElse(EmandateResponseCode.UNKNOWN.getValue()));
        logger.info(emandateResponse.toString());
        return emandateResponse;
    }

    public EmandateResponse setEmandateResponse(ResponseEntity responseEntity)
    {
        Mandate mandate =null;
        String errorMsg = null;
        if (responseEntity.getBody() instanceof Mandate) {
            mandate = (Mandate) responseEntity.getBody();
        }
        if (responseEntity.getBody() instanceof String) {
            errorMsg = (String) responseEntity.getBody();
        }

        return new EmandateResponse(responseEntity.getStatusCode().toString(),mandate.getMandateCode(),errorMsg);
    }

    public Mandate findByMandateCode(String mandateCode)
    {
        try {
            return mandateRepo.findByMandateCode(mandateCode);
        }catch (Exception e)
        {
            logger.error("--Exception occured-- ",e);
            return null;
        }
    }

    public  ResponseEntity<?> getMandateStatus(StatusRequest statusRequest, UserType userType){
        StatusResponse statusResponse = null;
        Mandate mandate=null;
        try {
            switch (userType) {
                case BANK:
                    authenticateBank(statusRequest.getAuth());
                    mandate = findByMandateCode(statusRequest.getMandateCode());
                    if (mandate == null) {
                        statusResponse = new StatusResponse(statusRequest.getMandateCode(), EmandateResponseCode.MANDATE_NOT_FOUND.getValue(), EmandateResponseCode.MANDATE_NOT_FOUND.getCode());

                        return ResponseEntity.status(404).body(statusResponse);
                    }
                    statusResponse = new StatusResponse(mandate.getMandateCode(), mandate.getStatus().getName(), mandate.getStatus().getCode());

                    break;
                case BILLER:

                    authenticateBiller(statusRequest.getAuth());
                    mandate = findByMandateCode(statusRequest.getMandateCode());
                    if (mandate == null) {
                        statusResponse = new StatusResponse(statusRequest.getMandateCode(), EmandateResponseCode.MANDATE_NOT_FOUND.getValue(), EmandateResponseCode.MANDATE_NOT_FOUND.getCode());
                        return ResponseEntity.status(404).body(statusResponse);
                    }
                    statusResponse = new StatusResponse(mandate.getMandateCode(), mandate.getStatus().getName(), mandate.getStatus().getCode());
                    break;
            }
            return ResponseEntity.status(200).body(statusResponse);
        }catch (CMMSException e)
        {
            e.printStackTrace();
            statusResponse = new StatusResponse(mandate.getMandateCode(), mandate.getStatus().getName(), mandate.getStatus().getCode());
            return ResponseEntity.status(Integer.valueOf(e.getCode())).body(statusResponse);
        }
    }


    public Mandate generateMandate(Mandate mandate, EmandateRequest emandateRequest,
                                   String mandateCode, Product product, Bank bank, Biller biller,Channel channel) throws CMMSException {

        logger.info("Generating mandate request ===============");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            mandate.setStartDate(dateFormat.parse(emandateRequest.getStartDate()));
            logger.info("Mandate start date: "+mandate.getStartDate());
            mandate.setEndDate(dateFormat.parse(emandateRequest.getEndDate()));
            logger.info("Mandate start date: "+mandate.getEndDate());
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("Error track ---",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500",EmandateResponseCode.PARSE_DATE_ERROR.getCode());
        }
        mandate.setEmail(emandateRequest.getEmailAddress());
        mandate.setAccountNumber(emandateRequest.getAccountNumber());
        //TODO make sure you remove Optional.ofNullable when deploying to the server
        //TODO add this one
        String accountName =null;
        try {
            accountName = EMandateValidator.generateAccountName(emandateRequest).getAccountName();
        }catch (CMMSException e)
        {
            throw new CMMSException(e.getMessage(),"404",EmandateResponseCode.INVALID_ACCOUNT_NUMBER.getCode());
        }
        if (accountName ==null)
        {
            throw new CMMSException("Wrong account number","400",EmandateResponseCode.INVALID_ACCOUNT_NUMBER.getCode());
        }
        mandate.setAccountName(accountName);
        mandate.setBank(new Bank(bank.getId(),bank.getCode(),bank.getName(),bank.getNipBankCode()));   //subscriber's bank
        mandate.setLastActionBy(null);
        mandate.setLastActionBy(null);
//        mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getEntityType()));
        mandate.setPayerName(emandateRequest.getPayerName());
        mandate.setPayerAddress(emandateRequest.getPayerAddress());
        mandate.setProduct(new Product(product.getId(),product.getName(),product.getAmount(),product.getDescription()));
        mandate.setPhoneNumber(emandateRequest.getPhoneNumber());
        mandate.setNarration(emandateRequest.getNarration());
        if (emandateRequest.isFixedAmountMandate())
        {
            mandate.setAmount(emandateRequest.getAmount());
            mandate.setFrequency(emandateRequest.getFrequency());
            mandate.setMandateType(MandateRequestType.FIXED);
            if (mandate.getFrequency() > 0) {
                Date nextDebitDate = DateUtils.calculateNextDebitDate(mandate.getStartDate(), mandate.getEndDate(),
                        mandate.getFrequency());
                mandate.setNextDebitDate(nextDebitDate == null ? DateUtils.lastSecondOftheDay(mandate.getEndDate())
                        : DateUtils.nullifyTime(nextDebitDate));
            }

        }else
        {
            mandate.setMandateType(MandateRequestType.VARIABLE);
            mandate.setFrequency(0);
            mandate.setVariableAmount(emandateRequest.getAmount());
        }
        mandate.setChannel(channel);
        mandate.setBiller(biller);
        mandate.setServiceType(ServiceType.PREPAID);
        mandate.setFixedAmountMandate(emandateRequest.isFixedAmountMandate());
        MandateStatus mandateStatus = null;
        mandateStatus = mandateStatusService.getMandateStatusByStatusName(MandateStatusType.BANK_APPROVE_MANDATE);
        if (mandateStatus==null)
        {
            logger.info("Failed: Could not retrieve mandate status from DB");
            return null;
        }
        mandate.setStatus(mandateStatus);
        mandate.setWorkflowStatus(mandateStatus.getName());
        mandate.setSubscriberCode(emandateRequest.getSubscriberCode());
        mandate.setRequestStatus(Constants.STATUS_ACTIVE);
        mandate.setMandateCode(mandateCode);
        mandate.setDateAuthorized(new Date());
        mandate.setDateCreated(new Date());
        mandate.setDateAccepted(new Date());
        mandate.setDateApproved(new Date());
        mandate.setRejection(null);
        mandate.setBankNotified(1);
        mandate.setBillerNotified(1);
        mandate.setCreatedBy(null);
        logger.info("Generated mandate request: "+mandate);
        return mandate;
    }
    public void sendMandateAdvice(Mandate mandate)
    {
        String mandateInString = CommonUtils.convertObjectToJson(mandate);
        try {
            queueService.sendMandateAdvice(mandateInString);
        } catch (JMSException e) {
            logger.error("JSM exception occured while sending mandate advice for mandate code {}",mandate.getMandateCode(),e);
        }
    }
}
