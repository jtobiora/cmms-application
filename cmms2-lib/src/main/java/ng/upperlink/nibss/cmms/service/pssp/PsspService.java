package ng.upperlink.nibss.cmms.service.pssp;

import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.pssp.PsspRequest;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.makerchecker.*;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.Industry;
import ng.upperlink.nibss.cmms.model.pssp.Pssp;
import ng.upperlink.nibss.cmms.repo.pssp.PsspRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.IndustryService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import ng.upperlink.nibss.cmms.service.makerchecker.UserAuthorizationService;
import ng.upperlink.nibss.cmms.service.nibss.NibssUserService;
import ng.upperlink.nibss.cmms.util.AccountLookUp;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class PsspService {


    private static Logger logger = LoggerFactory.getLogger(PsspService.class);
    
    private UserService userService;
    private BankService bankService;
    private BankUserService bankUserService;
    private NibssUserService nibssUserService;
    private PsspUserService psspUserService;
    private PsspRepo psspRepo;
    private IndustryService industryService;
    @Value("${nibss-identity-key}")
    private String nibssId;
    private AccountLookUp accountLookUp;

    private OtherAuthorizationService otherAuthorizationService;
    @Autowired
    public void setOtherAuthorizationService(OtherAuthorizationService otherAuthorizationService) {
        this.otherAuthorizationService = otherAuthorizationService;
    }

    @Autowired
    public void setIndustryService(IndustryService industryService) {
        this.industryService = industryService;
    }

    @Autowired
    public void setPsspUserService(PsspUserService psspUserService){
        this.psspUserService = psspUserService;
    }
    
    @Autowired
    public void setPsspRepo(PsspRepo psspRepo){
        this.psspRepo = psspRepo;
    }

    @Autowired
    public void setAccountLookUp(AccountLookUp accountLookUp){
        this.accountLookUp = accountLookUp;
    }
    
    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setBankUserService(BankUserService bankUserService) {
        this.bankUserService = bankUserService;
    }

    @Autowired
    public void setNibssUserService(NibssUserService nibssUserService) {
        this.nibssUserService = nibssUserService;
    }
    
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Pssp save(Pssp pssp) {
        return psspRepo.save(pssp);
    }

    public Page<Pssp> getAllActive(boolean status, Pageable pageable) {
        return psspRepo.getActivePssp(status,AuthorizationStatus.CREATION_REJECTED, pageable);
    }

    public List<Pssp> getAllActive(boolean status) {
        return psspRepo.getActivePssp(status,AuthorizationStatus.CREATION_REJECTED);
    }

    public Page<Pssp> getPsspByBillerOwner(String psspOwner, Pageable pageable) {
        return psspRepo.getByPsspOwner(psspOwner, pageable,AuthorizationStatus.CREATION_REJECTED);
    }

    public List<Pssp> getPsspByBillerOwner(String psspOwner) {
        return psspRepo.getByPsspOwner(psspOwner,AuthorizationStatus.CREATION_REJECTED);
    }

    public Pssp getByApiKey(String apiKey)
    {
        return psspRepo.getAllByApiKey(apiKey);
    }

    public Page<Pssp> getAll(Pageable pageable) {
        return psspRepo.findAll(pageable);
    }

    public Page<Pssp> getAllPSSPByOwner(boolean activeStatus,String owner,Pageable pageable) {
        return psspRepo.getByPsspOwner(owner,pageable,AuthorizationStatus.CREATION_REJECTED);
    }

    public long countAccountNumber(String accountNumber) {
        return psspRepo.countAccountNumber(accountNumber);
    }
    public long countAccountNumber(String accountNumber,Long id) {
        return psspRepo.countAccountNumber(accountNumber,id);
    }

    public Pssp getPSSPById(Long id) {
        return psspRepo.findOne(id);
    }

    public long getCountOfSameRCNumber(String rcNumber, Long id) {
        if (id == null) {
            return countOfSameRCNumber(rcNumber);
        }
        return countOfSameRCNumber(rcNumber, id);
    }

    public Pssp getPsspByCode(String code) {
        return psspRepo.getPsspByCode(code);
    }

    public Pssp getPSSPByOwner(String owner) {
        return psspRepo.getPSSPByOwner(owner);
    }

    public ResponseEntity<?> toggle(Long id, UserDetail userDetail) throws CMMSException {
        User operatorUser = userService.get(userDetail.getUserId());
        if (operatorUser == null){
            return ResponseEntity.status(404).body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }
        
        authenticate(null,InitiatorActions.TOGGLE,operatorUser);
        Pssp psspToBeToggled = this.getPSSPById(id);
        
        if (psspToBeToggled == null) {
            return ResponseEntity.status(404).body(new ErrorDetails("Toggling failed, Pssp not found"));
        }

        psspToBeToggled = (Pssp)otherAuthorizationService.actions(null,psspToBeToggled,operatorUser,null,InitiatorActions.TOGGLE,null,EntityType.PSSP);

        return ResponseEntity.ok(psspToBeToggled);
        
    }

    public Pssp togglePssp(Pssp psspToBeToggled, User user) {
        boolean activated = psspToBeToggled.isActivated() ;
        psspToBeToggled.setUpdatedBy(user);
        psspToBeToggled.setActivated(!activated);
        return save(psspToBeToggled);
    }

    public void validate(PsspRequest request, boolean isUpdate, Long id) throws CMMSException {
        long rcNumberCount = 0;
        long accountNumberCount = 0;
        long bvnCount = 0;
        long nameCount =0;
        if (StringUtils.isEmpty(request.getRcNumber()))
        {
            throw new CMMSException("RC Number is not provided.","400","400");
        }
        if (request.getAccountNumber() == null || request.getAccountNumber().isEmpty())
        {
            throw new CMMSException("The account number of the PSSP was not provided.","400","400");
        }
        if (isUpdate) {
            if (id == null)
            {
                throw new CMMSException("Pssp id is not provided.","400","400");
            }
            rcNumberCount = countOfSameRCNumber(request.getRcNumber(), request.getId());
            accountNumberCount = countAccountNumber(request.getAccountNumber(),request.getId());
        } else {
            rcNumberCount = countOfSameRCNumber(request.getRcNumber());
            accountNumberCount = countAccountNumber(request.getAccountNumber());
        }

        if (rcNumberCount > 0)
        {
            throw new CMMSException("This RC Number '" + request.getRcNumber() + "' already exists!","400","400");
        }

        if (accountNumberCount > 0)
        {
            throw new CMMSException("This account number '" + request.getAccountNumber() + "' already exists!","400","400");
        }
    }

    public Pssp generate(Pssp newPssp, Pssp existingPssp,PsspRequest request, User operator, boolean isUpdate) throws CMMSException {
        try {
            generateUpdate(newPssp, request);
            if (isUpdate) {
//            newPssp.setUpdatedAt(new Date());
                newPssp.setUpdatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.isActivated(), operator.getUserType()));
                String jsonData = JsonBuilder.generateJson(newPssp);
                return (Pssp) otherAuthorizationService.actions(jsonData, existingPssp, operator, null, InitiatorActions.UPDATE, null, EntityType.PSSP);
            } else {
                String psspCode = EncyptionUtil.generateString(6, true, true);
                newPssp.setPsspCode(psspCode);
                newPssp.setRcNumber(request.getRcNumber());
                newPssp.setBvn(request.getBvn());
                newPssp.setActivated(false);
                newPssp.setApiKey(psspCode + EncyptionUtil.generateString(20, true, true));
                newPssp.setCreatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.isActivated(), operator.getUserType()));
                setBank(newPssp, request, operator);
                return (Pssp) otherAuthorizationService.actions(null, newPssp, operator, null, InitiatorActions.CREATE, null, EntityType.PSSP);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Error track ---- ",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
        }
    }

    public void generateUpdate(Pssp pssp, PsspRequest request) {
        pssp.setPsspName(request.getPsspName());
        pssp.setAccountName(request.getAccountName());
        pssp.setAccountNumber(request.getAccountNumber());
        pssp.setDescription(request.getDescription());
        pssp.setIndustry(request.getIndustry());
    }

    public Pssp generateApproved(Pssp pssp, Pssp fromJson ,User operator) {
        pssp.setPsspName(fromJson.getPsspName());
        pssp.setAccountName(fromJson.getAccountName());
        pssp.setAccountNumber(fromJson.getAccountNumber());
        pssp.setDescription(fromJson.getDescription());
        pssp.setIndustry(fromJson.getIndustry());
        return pssp;
    }

    public void setBank(Pssp pssp, PsspRequest request, User operator) throws CMMSException {
        Bank bank = null;
        switch (operator.getUserType())
        {
            case BANK:
                BankUser bankUser = (BankUser)operator;
                bank = bankUser.getUserBank();

                if (bank == null) {
                    throw new CMMSException("No bank is retrieved from the logged in user to tie to this PSSP ","500","500");
                }
                break;
            case NIBSS:
                bank= bankService.getBankByCode(request.getBankCode());
                if (bank == null) {
                    throw new CMMSException("No bank is provided to tie PSSP","400","400");
                }
                pssp.setPsspOwner(nibssId);
                break;
        }
        pssp.setBank(new Bank(bank.getId(),bank.getCode(),bank.getName(),bank.getApiKey()));
    }

    public ResponseEntity<?> setup(PsspRequest psspRequest, boolean isUpdate, UserDetail userDetail, AuthorizationAction action, InitiatorActions initiatorActions) throws CMMSException {

//        BankUser operatorUserBank = null;
//        NibssUser operatorUserNibss = null;
        User operator = userService.get(userDetail.getUserId());
        if (operator ==null)
        {
            throw new CMMSException("Please login and try again","401","401");
        }
        authenticate(action, initiatorActions, operator);
        validate(psspRequest, isUpdate, psspRequest.getId());

        Industry industry = industryService.getOne(psspRequest.getIndustryId());
        if (industry == null)
        {
            throw new CMMSException("Please chose an Industry","400","400");
        }
        psspRequest.setIndustry(industry);

        Pssp existingPssp = null;
        Pssp newPssP = new Pssp();
        if (isUpdate) {
            existingPssp = this.getPSSPById(psspRequest.getId());

            if (existingPssp == null) {
                throw new CMMSException(Errors.DATA_IS_NULL.getValue().replace("{}", "Pssp to be updated "),"400","400");
            }
            existingPssp = generate(newPssP,existingPssp, psspRequest, operator, true);
            existingPssp= save(existingPssp);
            return ResponseEntity.ok().body(existingPssp);

        } else
            {
                newPssP = generate(newPssP,null, psspRequest, operator, false);
                System.out.println("printing the content of PSSP");
                System.out.println(newPssP);
                newPssP = save(newPssP);
                return ResponseEntity.status(201).body(newPssP);

            }
    }

    public void authenticate(AuthorizationAction action, InitiatorActions initiatorActions, User operator) throws CMMSException {
        Collection<Role> roles = operator.getRoles();


        Role operatorRole = roles.stream().findAny().get();

        RoleName[] initiatorRoles = {RoleName.BANK_ADMIN_INITIATOR,RoleName.NIBSS_INITIATOR};
        RoleName[] authorizerRoles = {RoleName.BANK_ADMIN_AUTHORIZER,RoleName.NIBSS_AUTHORIZER};

        if (!Arrays.asList(initiatorRoles).contains(operatorRole.getName())&&!Arrays.asList(authorizerRoles).contains(operatorRole.getName()))
        {
            throw new CMMSException(Errors.NOT_PERMIT.getValue().replace("{}","PSSP"),"401","401");
        }
        if (Arrays.asList(UserAuthorizationService.INITIATE_ACTIONS).contains(initiatorActions)&&!Arrays.asList(initiatorRoles).contains(operatorRole.getName()))
        {
            throw new CMMSException(Errors.NOT_PERMITTED_TO_CREATE.getValue().replace("{}","PSSP"),"401","401") ;
        }
        if (Arrays.asList(UserAuthorizationService.APPROVE_ACTIONS).contains(action)||
                Arrays.asList(UserAuthorizationService.REJECT_ACTIONS).contains(action))
        {
            if (!Arrays.asList(authorizerRoles).contains(operatorRole.getName()))
            {
                throw new CMMSException( Errors.NOT_PERMITTED_TO_AUTHORIZE.getValue().replace("{}","PSSP"),"401","401");
            }
        }
    }

    public ResponseEntity<?> getPsspList(UserDetail userDetail) {
        List<Pssp> psspList =null;
        switch (UserType.valueOf(userDetail.getUserType()))
        {
            case BANK:
                BankUser operatorBankUser  = bankUserService.getById(userDetail.getUserId());
                String psspOwner = operatorBankUser.getUserBank().getApiKey();
                psspList = getPsspByBillerOwner(psspOwner);
                break;
            case NIBSS:
                psspList = getPsspByBillerOwner(nibssId);
                break;
        }
        if (psspList.isEmpty())return ResponseEntity.status(404).body(new ErrorDetails("Could not retrieve the pssp"));

        return ResponseEntity.ok(psspList);
    }

    public  Long countOfSameRCNumber(String rcNumber){
        return psspRepo.countOfSameRCNumber(rcNumber);
    }

    public  Long countOfSameRCNumber(String rcNumber,Long id){
        return psspRepo.countOfSameRCNumber(rcNumber,id);
    }

    public Page<Pssp> selectView(UserDetail userDetail,ViewAction viewAction, Pageable pageable)
    throws CMMSException{
        User operator = userService.get(userDetail.getUserId());
        String psspOwner = null;
        if (operator == null)
        {
            throw new CMMSException("Please Login and try again","401","401");
        }
        switch (operator.getUserType())
        {
            case NIBSS: psspOwner = nibssId;
                break;
            case BANK:
                BankUser bankUser = (BankUser)operator;
                psspOwner = bankUser.getUserBank().getApiKey();
                break;
            default:
                throw new CMMSException("Unknown user type found","400","400");
        }
        if (psspOwner ==null)
        {
            throw new CMMSException("Pssp owner is not found","404","404");
        }
        switch (viewAction)
        {
            case UNAUTHORIZED:
                return this.getAllPending(psspOwner,pageable);
            case AUTHORIZED:
                return getAllApproved(psspOwner,AuthorizationStatus.AUTHORIZED,pageable);
            case REJECTED:
                return getAllRejected(psspOwner,Arrays.asList(UserAuthorizationService.rejectionStatuses),pageable);
            default:
                return getAllApproved(psspOwner,AuthorizationStatus.AUTHORIZED,pageable);
        }
    }
    private  Page<Pssp> getAllPending(String psspOwner,Pageable pageable){
        return this.psspRepo.getAllPending(psspOwner,Arrays.asList(UserAuthorizationService.pendingActions),pageable);
    }

    private Page<Pssp> getAllApproved(String psspOwner,AuthorizationStatus authStatus, Pageable pageable)
    {
        return psspRepo.getAllApproved(psspOwner,authStatus,pageable);
    }

    private  Page<Pssp> getAllRejected(String psspOwner,List<AuthorizationStatus> authStatusList, Pageable pageable)
    {
        return psspRepo.getAllRejected(psspOwner,authStatusList,pageable);
    }

    public Pssp previewUpdate(Long id) throws CMMSException {
        Pssp bankRetrieved = psspRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        String jsonData = bankRetrieved.getJsonData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Pssp jsonUser = mapper.readValue(jsonData, Pssp.class);
            return jsonUser;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error track ----",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
        }
    }

    long countAllByBillerId(Long id)
    {
        return psspRepo.countAllByPsspId(id);
    }

    long countAllByBillerIdAndStatus(boolean status,String psspOwner)
    {
        return psspRepo.countAllByPsspIdAndStatus(status,psspOwner);
    }
    long countAll(Long id)
    {
        return psspRepo.countAll(id);
    }

    long countAllByStatus(boolean status)
    {
        return psspRepo.countAllByStatus(status);
    }
}
