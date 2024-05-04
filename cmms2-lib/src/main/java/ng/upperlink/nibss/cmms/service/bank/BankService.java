package ng.upperlink.nibss.cmms.service.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.bank.BankRequest;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.makerchecker.*;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.repo.bank.BankRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import ng.upperlink.nibss.cmms.service.makerchecker.UserAuthorizationService;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@Service
public class BankService {

    private static Logger logger = LoggerFactory.getLogger(BankService.class);
    private BankRepo bankRepo;

    private UserService userService;
    private BankService bankService;
    private OtherAuthorizationService otherAuthorizationService;

    @Autowired
    public void setOtherAuthorizationService(OtherAuthorizationService otherAuthorizationService) {
        this.otherAuthorizationService = otherAuthorizationService;
    }
    @Autowired
    public void setBankRepo(BankRepo bankRepo) {
        this.bankRepo = bankRepo;
    }


    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }
    @Autowired
    public void setUserService(UserService userService)
    {
        this.userService = userService;
    }

    public Page<Bank> getBankByPropName(String propName , Pageable pageable)
    {
        return bankRepo.getBankByAnyPropName(propName, pageable);
    }
    public Page<Bank> getAllActivated(boolean activeStatus, Pageable pageable)
    {
        return bankRepo.getAllActiveStatus(activeStatus,AuthorizationStatus.CREATION_REJECTED,pageable);
    }
    public List<Bank> getAllActivated(boolean activeStatus)
    {
        return bankRepo.getAllActiveStatus(activeStatus,AuthorizationStatus.CREATION_REJECTED);
    }
    public Page<Bank> getAll(Pageable pageable)
    {
        return bankRepo.findAll(pageable);
    }
    public Bank getByBankId(Long id)
    {
        return bankRepo.getBankById(id);
    }
    public Bank getBankByCode(String code)
    {
        return bankRepo.getBankByCode(code);
    }

    public Bank getBankByDomain(String domainName)
    {
        return bankRepo.getBankByDomainName(domainName);
    }
    public Bank getBankByUsername(String username)
    {
        return bankRepo.getBankByUserName(username);
    }
//    public long getCountBankByDomainName(String domainName)
//    {
//        return bankRepo.countByDomainName(domainName);
//    }
//    public long getCountBankByDomain(String domainName, Long id)
//    {
//        return bankRepo.countByDomainName(domainName,id);
//    }

    public long getCountBankByCode(String code)
    {
        return bankRepo.countByCode(code);
    }
    public long getCountBankByCode(String code, Long id)
    {
        return bankRepo.countByCode(code,id);
    }
    public Bank save(Bank bank)
    {
        return bankRepo.save(bank);
    }
    public ResponseEntity<?> toggle(Long id,UserDetail userDetail,boolean isEmandate) throws CMMSException {
        User operatorUser = userService.get(userDetail.getUserId());
        if (operatorUser == null){
            throw new CMMSException(Errors.UNKNOWN_USER.getValue(),"404","404");
        }
        authenticate(operatorUser,null,InitiatorActions.TOGGLE);
        Bank bankToBeToggled = this.getByBankId(id);
        if (bankToBeToggled == null)
                throw new CMMSException("Toggling failed, Bank not found","404","404");

        bankToBeToggled = (Bank)otherAuthorizationService.actions(null,bankToBeToggled,operatorUser,null,InitiatorActions.TOGGLE,null,EntityType.BANK);

            return ResponseEntity.ok(save(bankToBeToggled));
    }

    public Bank getByApiKey(String apiKey)
    {
        return bankRepo.getAllByApiKey(apiKey);
    }
    public Bank toggleBank(Bank bankToBeToggled, User user)
    {
        boolean activate = bankToBeToggled.isActivated() ;
        bankToBeToggled.setUpdatedBy(user);
        bankToBeToggled.setActivated(!activate);
        return  save(bankToBeToggled);
    }

//    public Bank toggleEmandate(Bank bankToBeToggled, User operatorUser)
//    {
//        boolean emandateEnabled = bankToBeToggled.isEmandateEnabled() ;
//        bankToBeToggled.setEmandateEnabled(!emandateEnabled);
//        bankToBeToggled.setUpdatedBy(operatorUser);
//        return save(bankToBeToggled);
//    }

    public void validate(BankRequest request, boolean isUpdate) throws CMMSException {

        long bankCodeCount = 0;
        long bankNameCount = 0;
        long bankNipBankCodeCount =0;

        if (isUpdate){
            bankNameCount = countBankName(request.getName(),request.getId());
            bankCodeCount = getCountBankByCode(request.getCode(),request.getId());
            bankNipBankCodeCount = countNipCode(request.getCode(),request.getId());
        }else {
            bankNameCount = countBankName(request.getName());
            bankCodeCount = getCountBankByCode(request.getCode());
            bankNipBankCodeCount = countNipCode(request.getCode());
        }
            if (bankCodeCount > 0) {
                throw new CMMSException("Bank Code "+ request.getCode() + "' already exist","400","400");

        }
            if (bankNameCount > 0) {
                throw new CMMSException("Bank Name "+ request.getName() + "' already exist","400","400");

        }
            if (bankNipBankCodeCount > 0) {
                throw new CMMSException("Bank Nip code "+ request.getNipBankCode() + "' already exist","400","400");

        }
    }
    public Bank generate(Bank newBank,Bank existingBank, BankRequest request, User operator, boolean isUpdate) throws IOException, CMMSException {
        newBank = generateUpdate(newBank,request,operator);
        if(isUpdate)
        {
            newBank.setUpdatedBy(new User(operator.getId(),operator.getName(),  operator.getEmailAddress(), operator.isActivated(), operator.getUserType()));
//            newBank.setUpdatedAt(new Date());
            String jsonData = JsonBuilder.generateJson(newBank);
            logger.info("The generated json data: \n"+jsonData);
            return  (Bank)otherAuthorizationService.actions(jsonData,existingBank,operator,null,InitiatorActions.UPDATE,null,EntityType.BANK);
        }
        else {
            newBank.setActivated(false);
//            newBank.setEmandateEnabled(true);
//            newBank.setCreatedAt(new Date());
            newBank.setCreatedBy(new User(operator.getId(),operator.getName(),  operator.getEmailAddress(), operator.isActivated(), operator.getUserType()));
            newBank.setApiKey(request.getCode() + EncyptionUtil.generateString(20,true,true));
//            newBank.setClientPassKey(request.getCode()+EncyptionUtil.generateString(20,true,true));
            return  (Bank)otherAuthorizationService.actions(null,newBank,operator,null,InitiatorActions.CREATE,null,EntityType.BANK);
        }
    }
    public Bank generateApproved(Bank bankToBeUpdated, Bank fromJson, User operator){
        bankToBeUpdated.setName(fromJson.getName());
//        bankToBeUpdated.setNotificationUrl(fromJson.getNotificationUrl());
        bankToBeUpdated.setUpdatedBy(new User(operator.getId(),operator.getName(),  operator.getEmailAddress(), operator.isActivated(), operator.getUserType()));
        bankToBeUpdated.setNipBankCode(fromJson.getNipBankCode());
        bankToBeUpdated.setCode(fromJson.getCode());

        return bankToBeUpdated;
    }
    public Bank generateUpdate(Bank newBank, BankRequest bankRequest, User operator) throws JsonProcessingException {
        newBank.setName(bankRequest.getName());
//        newBank.setNotificationUrl(bankRequest.getNotificationUrl());
        newBank.setNipBankCode(bankRequest.getNipBankCode());
        newBank.setCode(bankRequest.getCode());
        return newBank;
    }
    public ResponseEntity<?> setup(BankRequest bankRequest, UserDetail userDetail, boolean isUpdate,
                                   AuthorizationAction action, InitiatorActions initiatorActions, AuthorizationRequest authorizationRequest) throws  CMMSException {

        Bank newBank= new Bank();
        User operatorUser = userService.get(userDetail.getUserId());
        if (operatorUser == null){
            throw new CMMSException("Please login and try again","401","401");
        }
        authenticate(operatorUser,action,initiatorActions);

        //make sure that the bank code is not null and doesn't already exist

        if (isUpdate)
        {
            if (bankRequest.getId() == 0 || bankRequest.getId() ==null){
                throw new CMMSException("Bank id is not provided","400","400");
            }
           Bank existingBank = bankService.getByBankId(bankRequest.getId());
            if (existingBank ==null)
            {
                throw new CMMSException("Bank you want to update does not exist","404","404");
            }
            validate(bankRequest, isUpdate);
            try {
                existingBank = generate(newBank,existingBank,bankRequest,operatorUser,isUpdate);
            } catch (IOException e) {
                logger.error("IOException {}",e);
                throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
            }
            existingBank = save(existingBank);
            return ResponseEntity.ok(existingBank);
        }
        else {


            if (bankRequest.getCode() == null || bankRequest.getCode().isEmpty()) {
                throw new CMMSException("Bank code is not provided","400","400");
            }
            validate(bankRequest, isUpdate);
            try {
                newBank = bankService.generate(newBank,null, bankRequest, operatorUser, isUpdate);
            } catch (IOException e) {
                logger.error("IOException {}",e);
                throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
            }
            newBank = bankService.save(newBank);
            return ResponseEntity.ok(newBank);
        }
    }
    public void authenticate(User operatorUser, AuthorizationAction action, InitiatorActions initiatorActions) throws CMMSException{
        Collection<Role> roles = operatorUser.getRoles();
        if (roles == null){
            throw new CMMSException("Please login and try again; operator role not found", "401", "401");
        }
        Role operatorRole = roles.stream().findAny().get();
        if (initiatorActions !=null && !operatorRole.getName().equals(RoleName.NIBSS_INITIATOR))
        {
                throw new CMMSException("Your are not authorized to create or toggle bank", "401", "401");
        }
        if (action !=null && !operatorRole.getName().equals(RoleName.NIBSS_AUTHORIZER))
        {
                throw new CMMSException("Your are not permitted  to authorize or reject bank",  "401", "401");
        }
    }
    public Page<Bank> searchBankEntity(String code, String name,String nipBankCode,boolean activated,boolean flag,Pageable pageable){
        return flag ? this.bankRepo.searchBankEntity(code, name, nipBankCode, pageable) :
                this.bankRepo.searchBankEntityUsingStatus(code, name, activated, nipBankCode, pageable);

    }
    public Page<Bank> selectView(ViewAction viewAction, Pageable pageable)
    {
        switch (viewAction)
        {
            case UNAUTHORIZED:
                return this.getAllPending(pageable);
            case AUTHORIZED:
                return getAllApproved(AuthorizationStatus.AUTHORIZED,pageable);
            case REJECTED:
                return getAllRejected(Arrays.asList(UserAuthorizationService.rejectionStatuses),pageable);
            default:
                return getAllApproved(AuthorizationStatus.AUTHORIZED,pageable);
        }
    }
    private  Page<Bank> getAllPending(Pageable pageable){
        return this.bankRepo.getAllPending(Arrays.asList(UserAuthorizationService.pendingActions),pageable);
    }

    private Page<Bank> getAllApproved(AuthorizationStatus authStatus, Pageable pageable)
    {
        return bankRepo.getAllApproved(authStatus,pageable);
    }

    private  Page<Bank> getAllRejected(List<AuthorizationStatus> authStatusList, Pageable pageable)
    {
        return bankRepo.getAllRejected(authStatusList,pageable);
    }
 public Page<Bank> selectViewConfig(ViewAction viewAction, Pageable pageable)
    {
        switch (viewAction)
        {
            case UNAUTHORIZED:
                return this.getAllPendingConfig(pageable);
            case AUTHORIZED:
                return getAllApprovedConfig(AuthorizationStatus.AUTHORIZED,pageable);
            case REJECTED:
                return getAllRejectedConfig(Arrays.asList(UserAuthorizationService.rejectionStatuses),pageable);
            default:
                return getAllApproved(AuthorizationStatus.AUTHORIZED,pageable);
        }
    }
    private  Page<Bank> getAllPendingConfig(Pageable pageable){
        return this.bankRepo.getAllPendingConfig(Arrays.asList(UserAuthorizationService.pendingActions),pageable);
    }

    private Page<Bank> getAllApprovedConfig(AuthorizationStatus authStatus, Pageable pageable)
    {
        return bankRepo.getAllApprovedConfig(authStatus,pageable);
    }

    private  Page<Bank> getAllRejectedConfig(List<AuthorizationStatus> authStatusList, Pageable pageable)
    {
        return bankRepo.getAllRejectedConfig(authStatusList,pageable);
    }

    public Bank previewUpdate(Long id) throws CMMSException
    {
        Bank bankRetrieved = bankRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        if (bankRetrieved == null)
            throw new CMMSException("No bank to preview ","400","400");
        if (StringUtils.isEmpty(bankRetrieved.getJsonData()))
        {
            throw new CMMSException("No content to preview ","400","400");
//            return null;
        }
        String jsonData = bankRetrieved.getJsonData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Bank jsonUser = mapper.readValue(jsonData, Bank.class);
            return jsonUser;
        } catch (IOException e) {
            logger.error("IOException {}",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
        }
    }

    long countBankName(String bankName)
    {
        return bankRepo.countByName(bankName);
    }
    long countBankName(String bankName,Long id)
    {
        return bankRepo.countByName(bankName,id);
    }

    long countNipCode(String nipCode,Long id)
    {
        return bankRepo.countByNipBankCode(nipCode,id);
    }

    long countNipCode(String nipCode)
    {
        return bankRepo.countByNipBankCode(nipCode);
    }
}