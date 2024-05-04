package ng.upperlink.nibss.cmms.service.makerchecker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.makerchecker.*;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.Industry;
import ng.upperlink.nibss.cmms.model.biller.Product;
import ng.upperlink.nibss.cmms.model.emandate.EmandateConfig;
import ng.upperlink.nibss.cmms.model.pssp.Pssp;
import ng.upperlink.nibss.cmms.repo.makerchecker.AuthorizationRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import ng.upperlink.nibss.cmms.service.biller.IndustryService;
import ng.upperlink.nibss.cmms.service.biller.ProductService;
import ng.upperlink.nibss.cmms.service.emandate.ConfigureEmandateService;
import ng.upperlink.nibss.cmms.service.nibss.NibssUserService;
import ng.upperlink.nibss.cmms.service.pssp.PsspService;
import ng.upperlink.nibss.cmms.service.pssp.PsspUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class OtherAuthorizationService {
    private RoleService roleService;
    private NibssUserService nibssUserService;
    private BankService bankService;
    private UserService userService;
    private BankUserService bankUserService;
    private BillerService billerService;
    private BillerUserService billerUserService;
    private ProductService productService;
    private IndustryService industryService;
    private PsspService psspService;
    private PsspUserService psspUserService;
    private AuthorizationRepo authorizationRepo;
    private ConfigureEmandateService configureEmandateService;

    public static AuthorizationStatus[] rejectionStatuses = {
            AuthorizationStatus.CREATION_REJECTED,
            AuthorizationStatus.UPDATE_REJECTED,
            AuthorizationStatus.TOGGLE_REJECTED
    };
    public static AuthorizationAction[] REJECT_ACTIONS = {
            AuthorizationAction.REJECT_TOGGLE,
            AuthorizationAction.REJECT_UPDATE,
            AuthorizationAction.REJECT_CREATE
    };
    public static AuthorizationAction[] APPROVE_ACTIONS = {
            AuthorizationAction.APPROVE_TOGGLE,
            AuthorizationAction.APPROVE_CREATE,
            AuthorizationAction.APPROVE_UPDATE
    };
    public static InitiatorActions[] INITIATE_ACTIONS = {
            InitiatorActions.CREATE,
            InitiatorActions.UPDATE,
            InitiatorActions.TOGGLE
    };

    public static AuthorizationStatus[] pendingActions = {
            AuthorizationStatus.UNAUTHORIZED_CREATE,
            AuthorizationStatus.UNAUTHORIZED_TOGGLE,
            AuthorizationStatus.UNAUTHORIZED_UPDATE};
    @Autowired
    public void setAuthorizationRepo(AuthorizationRepo authorizationRepo) {
        this.authorizationRepo = authorizationRepo;
    }
    @Autowired
    public void setConfigureEmandateService(ConfigureEmandateService configureEmandateService) {
        this.configureEmandateService = configureEmandateService;
    }

    @Autowired
    public void setPsspService(PsspService psspService) {
        this.psspService = psspService;
    }

    @Autowired
    public void setPsspUserService(PsspUserService psspUserService) {
        this.psspUserService = psspUserService;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setIndustryService(IndustryService industryService) {
        this.industryService = industryService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setNibssUserService(NibssUserService nibssUserService) {
        this.nibssUserService = nibssUserService;
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
    public void setBillerService(BillerService billerService) {
        this.billerService = billerService;
    }

    @Autowired
    public void setBillerUserService(BillerUserService billerUserService) {
        this.billerUserService = billerUserService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    private void saveAction(AuthorizationTable authorizationTable)
    { 
      authorizationRepo.save(authorizationTable);
    }
    public AuthorizationTable getById(Long id)
    {
        return authorizationRepo.findOne(id);
    }
    public AuthorizationTable initiateAction(String jsonData, AuthorizationTable objToBeSaved, InitiatorActions action, EntityType entityType) throws JsonProcessingException, CMMSException {
        switch (action)
        {
            case CREATE:
               objToBeSaved=  setupAuthorizationTable(AuthorizationStatus.UNAUTHORIZED_CREATE,objToBeSaved, action,entityType,null);
                return objToBeSaved;
            case UPDATE:
                switch (objToBeSaved.getAuthorizationStatus())
                {
                    case UNAUTHORIZED_UPDATE:

                        objToBeSaved = setupAuthorizationTable(AuthorizationStatus.UNAUTHORIZED_UPDATE,objToBeSaved, action, entityType,jsonData);
                        return objToBeSaved;
                    case AUTHORIZED:
                        objToBeSaved = setupAuthorizationTable(AuthorizationStatus.UNAUTHORIZED_UPDATE,objToBeSaved, action, entityType,jsonData);
                        return objToBeSaved;
                    case CREATION_REJECTED:
                        objToBeSaved = setupAuthorizationTable(AuthorizationStatus.UNAUTHORIZED_CREATE,objToBeSaved, action, entityType,null);
                        return objToBeSaved;
                    case UNAUTHORIZED_CREATE:
                        return setupAuthorizationTable(AuthorizationStatus.UNAUTHORIZED_CREATE,objToBeSaved, action, entityType,null);
                    case UPDATE_REJECTED:
                        return setupAuthorizationTable(AuthorizationStatus.UNAUTHORIZED_UPDATE,objToBeSaved,InitiatorActions.UPDATE,entityType,jsonData);
                        default:
                        {
                            throw new CMMSException("Update failed : Cannot update object with :"
                                    +objToBeSaved.getAuthorizationStatus().getValue(),"400","400");
                        }

                }
            case TOGGLE:
                return getToggleAction(objToBeSaved,entityType);
                default:
                        {
                    throw new CMMSException(Errors.REQUEST_TERMINATED.getValue(),"400","400");
                         }
        }
    }

    public  AuthorizationTable getToggleAction(AuthorizationTable objToBeSaved,EntityType entityType) throws CMMSException {
        switch (objToBeSaved.getAuthorizationStatus())
        {
            case AUTHORIZED:
                return setupAuthorizationTable(AuthorizationStatus.UNAUTHORIZED_TOGGLE,objToBeSaved,InitiatorActions.TOGGLE,entityType,null);
            case UNAUTHORIZED_CREATE:
                throw new CMMSException(Errors.NO_ACTIONS_PERMITTED.getValue(),"400","400");
            case UNAUTHORIZED_UPDATE:
                throw new CMMSException(Errors.NO_ACTIONS_PERMITTED.getValue(),"400","400");
            case UNAUTHORIZED_TOGGLE:
                return setupAuthorizationTable(AuthorizationStatus.AUTHORIZED,objToBeSaved,null,entityType,null);

            case UPDATE_REJECTED:
                return setupAuthorizationTable(AuthorizationStatus.UNAUTHORIZED_TOGGLE,objToBeSaved,null,entityType,objToBeSaved.getJsonData());

                default:
                    throw new CMMSException(Errors.NO_ACTIONS_PERMITTED.getValue(),"400","400");
        }
    }

    public AuthorizationTable setupAuthorizationTable(AuthorizationStatus authorizationStatus, AuthorizationTable objToBeSaved,
                                                      InitiatorActions action, EntityType entityType, String jsonData) {
        objToBeSaved.setEntityType(entityType);
        objToBeSaved.setActionInitiated(action);
        objToBeSaved.setAuthorizationStatus(authorizationStatus);
        objToBeSaved.setJsonData(jsonData);

        return objToBeSaved;
    }

    public AuthorizationTable rejectAction(AuthorizationTable objectToBeUpdated, AuthorizationAction action, AuthorizationRequest request,EntityType entityType) throws CMMSException {
        switch (action)
        {
            case REJECT_CREATE:
                if (!objectToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.UNAUTHORIZED_CREATE))
                {
                    throw new CMMSException("Reject creation failed: This action is not permitted","401","401");
                }
               objectToBeUpdated= generateRejection(objectToBeUpdated, request, AuthorizationStatus.CREATION_REJECTED);
                return performSave(objectToBeUpdated,entityType);

            case REJECT_UPDATE:if (!objectToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.UNAUTHORIZED_UPDATE))
            {
                throw new CMMSException("Reject update failed: This action is not permitted","401","401");
            }
                    objectToBeUpdated = generateRejection(objectToBeUpdated, request, AuthorizationStatus.UPDATE_REJECTED);
                return performSave(objectToBeUpdated,entityType);

            case REJECT_TOGGLE:
                if (!objectToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.UNAUTHORIZED_TOGGLE))
                {
                    throw new CMMSException("Reject toggle failed: This action is not permitted","401","401");
                }
                objectToBeUpdated =generateRejection(objectToBeUpdated, request, AuthorizationStatus.TOGGLE_REJECTED);
                return performSave(objectToBeUpdated,entityType);

                default:
                {
                    throw new CMMSException(Errors.REQUEST_TERMINATED.getValue(),"400","400");
//                return null;
                }
        }
    }

    public AuthorizationTable generateRejection(AuthorizationTable objectToBeSaved, AuthorizationRequest request, AuthorizationStatus creationRejected) {
        objectToBeSaved.setAuthorizationStatus(creationRejected);
        objectToBeSaved.setReason(request.getReason());
        return objectToBeSaved;
    }

    public AuthorizationTable approveAction(AuthorizationTable objectToBeUpdated, AuthorizationAction action, User operaator, EntityType entityType) throws CMMSException {
        switch (action)
        {
            case APPROVE_CREATE:
                return approveCreate(objectToBeUpdated, entityType);
            case APPROVE_UPDATE:
                return approveUpdate(objectToBeUpdated, operaator, entityType);
            case APPROVE_TOGGLE:
                return approveToggle(objectToBeUpdated, entityType);
            default:
            {
                throw new CMMSException(Errors.REQUEST_TERMINATED.getValue(),"400","400");
            }
        }
    }

    public AuthorizationTable approveCreate(AuthorizationTable objectToBeUpdated, EntityType entityType) throws CMMSException {
        if (!objectToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.UNAUTHORIZED_CREATE))
        {
            throw new CMMSException(AuthorizationAction.APPROVE_CREATE.getValue().replace("{}",entityType.getValue())+" cannot be performed","400","400");
        }
        else {
            objectToBeUpdated.setActivated(true);
            objectToBeUpdated = setupAuthorizationTable(AuthorizationStatus.AUTHORIZED,objectToBeUpdated,null,entityType,null);
            return performSave(objectToBeUpdated,entityType);
        }
    }

    public AuthorizationTable approveToggle(AuthorizationTable objectToBeUpdated, EntityType entityType) throws CMMSException {
        boolean activated = objectToBeUpdated.isActivated();
        objectToBeUpdated.setActivated(!activated);
        objectToBeUpdated = setupAuthorizationTable(AuthorizationStatus.AUTHORIZED,objectToBeUpdated,null,entityType,null);
        objectToBeUpdated = performSave(objectToBeUpdated,entityType);
        return objectToBeUpdated;
    }

    public AuthorizationTable approveUpdate(AuthorizationTable objectToBeUpdated, User operaator, EntityType entityType) throws  CMMSException {
        if (!objectToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.UNAUTHORIZED_UPDATE)||
                objectToBeUpdated.getAuthorizationStatus().equals(AuthorizationStatus.AUTHORIZED)||
                StringUtils.isEmpty(objectToBeUpdated.getJsonData()))
        {
            throw new CMMSException("Failed: This action is not permitted for "+objectToBeUpdated.getAuthorizationStatus().getValue(),"401","401");
        }
        objectToBeUpdated = generateApprovedObject(objectToBeUpdated, operaator);
        objectToBeUpdated = setupAuthorizationTable(AuthorizationStatus.AUTHORIZED,objectToBeUpdated,null,entityType,null);
        objectToBeUpdated =  performSave(objectToBeUpdated,entityType);
        return objectToBeUpdated;
    }

    public AuthorizationTable generateApprovedObject(AuthorizationTable objectToBeUpdated, User operator) throws CMMSException {
        if (objectToBeUpdated instanceof Bank)
        {
            return generateBank(objectToBeUpdated, operator);
        } 
       else if (objectToBeUpdated instanceof  Biller) {
            return generateBiller(objectToBeUpdated, operator);
        }
        else if (objectToBeUpdated instanceof  Product)
        {
            return generateProduct(objectToBeUpdated, operator);
        }
        else if (objectToBeUpdated instanceof Industry)
        {
            return generateIndustry(objectToBeUpdated, operator);
        }
        else if (objectToBeUpdated instanceof Pssp)
        {
            return generatePssp(objectToBeUpdated, operator);
        }
        else if (objectToBeUpdated instanceof EmandateConfig)
        {
            return generateEmandateConfig(objectToBeUpdated, operator);
        }
        else {
                throw new CMMSException("Unknown object : ", "500", "500");
            } 
 
    }

    public AuthorizationTable generateIndustry(AuthorizationTable objectToBeUpdated, User operaator) throws CMMSException {
        try {
            Industry industryTobeUpdated = (Industry) objectToBeUpdated;
            String jsonData = industryTobeUpdated.getJsonData();
            ObjectMapper objectMapper = new ObjectMapper();
            Industry fromJson = objectMapper.readValue(jsonData, Industry.class);
            objectToBeUpdated = industryService.generateApproved(industryTobeUpdated, fromJson, operaator);
            return objectToBeUpdated;
        }catch (Exception e)
        {
            e.printStackTrace();
            log.error("Error track ----",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");

        }
    }

    public AuthorizationTable generatePssp(AuthorizationTable objectToBeUpdated, User operaator) throws CMMSException {
       try {
           Pssp industryTobeUpdated = (Pssp) objectToBeUpdated;
           String jsonData = industryTobeUpdated.getJsonData();
           ObjectMapper objectMapper = new ObjectMapper();
           Pssp fromJson = objectMapper.readValue(jsonData, Pssp.class);
           objectToBeUpdated = psspService.generateApproved(industryTobeUpdated, fromJson, operaator);
           return objectToBeUpdated;
       }catch (Exception e)
       {
           e.printStackTrace();
           log.error("Error track ----",e);
           throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");

       }
    }

    public AuthorizationTable generateEmandateConfig(AuthorizationTable objectToBeUpdated, User operator) throws CMMSException {
       try {
           EmandateConfig emandateConfigTobeUpdated = (EmandateConfig) objectToBeUpdated;
           String jsonData = emandateConfigTobeUpdated.getJsonData();
           ObjectMapper objectMapper = new ObjectMapper();
           EmandateConfig fromJson = objectMapper.readValue(jsonData, EmandateConfig.class);
           objectToBeUpdated = configureEmandateService.generateApproved(emandateConfigTobeUpdated, fromJson, operator);
           return objectToBeUpdated;
       }catch (Exception e)
       {
           e.printStackTrace();
           log.error("Error track ----",e);
           throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");

       }
    }

    public AuthorizationTable generateProduct(AuthorizationTable objectToBeUpdated, User operator) throws CMMSException {
       try {
           Product productToBeUpdated = (Product) objectToBeUpdated;
           String jsonData = productToBeUpdated.getJsonData();
           ObjectMapper objectMapper = new ObjectMapper();
           Product fromJson = objectMapper.readValue(jsonData, Product.class);
           objectToBeUpdated = productService.generateApproved(productToBeUpdated, fromJson, operator);
           return objectToBeUpdated;
       }catch (Exception e)
       {
           e.printStackTrace();
           log.error("Error track ----",e);
           throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");

       }
    }

    public AuthorizationTable generateBiller(AuthorizationTable objectToBeUpdated, User operaator) throws CMMSException {
       try {
           Biller billerToBeUpdated = (Biller) objectToBeUpdated;
           String jsonData = billerToBeUpdated.getJsonData();
           ObjectMapper objectMapper = new ObjectMapper();
           Biller biller = objectMapper.readValue(jsonData, Biller.class);
           objectToBeUpdated = billerService.generateApproved(billerToBeUpdated, biller, operaator);
           return objectToBeUpdated;
       }catch (Exception e)
       {
           e.printStackTrace();
           log.error("Error track ----",e);
           throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");

       }
    }

    public AuthorizationTable generateBank(AuthorizationTable objectToBeUpdated, User operaator) throws CMMSException {
        try {
            Bank bankToBeUpdated = (Bank) objectToBeUpdated;
            String jsonData = bankToBeUpdated.getJsonData();
            ObjectMapper objectMapper = new ObjectMapper();
            Bank newBank = objectMapper.readValue(jsonData, Bank.class);
            objectToBeUpdated = bankService.generateApproved(bankToBeUpdated, newBank, operaator);
            return objectToBeUpdated;
        }catch (Exception e)
        {
            e.printStackTrace();
            log.error("Error track ----",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");

        }
    }

    public AuthorizationTable performSave(AuthorizationTable objectToBeUpdated, EntityType entityType) throws CMMSException {
            switch (entityType) {
                case NIBSS_USER:
                    return null;//nibssUserService.save((NibssUser) objectToBeUpdated);
                case BANK_USER:
                    return null;//bankUserService.save((BankUser) objectToBeUpdated);
                case BILLER_USER:
                    return null;//billerUserService.save((BillerUser) objectToBeUpdated);
                case PSSP_USER:
//                    return psspUserService.save((PsspUser)objectToBeUpdated);
                case BANK:
                    return bankService.save((Bank) objectToBeUpdated);
                case PSSP:
                    return psspService.save((Pssp)objectToBeUpdated);
                case BILLER:
                    return billerService.save((Biller) objectToBeUpdated);
                case PRODUCT:
                    return productService.save((Product) objectToBeUpdated);
                case INDUSTRY:
                    return industryService.save((Industry) objectToBeUpdated);
                case EMANDATE:
                    return configureEmandateService.save((EmandateConfig) objectToBeUpdated);
                case SUBSCRIBER:
                    return null;
                default: {
                    throw new CMMSException("Object type not found", "500", "500");
                }
            }
        
    }

    public AuthorizationTable actions (String jsonData, AuthorizationTable objectToBeSaved, User operator, AuthorizationAction action,
                                       InitiatorActions initiatorActions, AuthorizationRequest request, EntityType entityType) throws  CMMSException {
            try {
                switch (selectAction(initiatorActions,action,request))
                {
                    case "INITIATE":
                        if (!operator.getRoles().stream().findAny().get().getUserAuthorisationType().equalsIgnoreCase("OPERATOR"))
                            throw new CMMSException(Errors.NOT_PERMITTED_TO_CREATE.getValue().replace("{}",entityType.getValue()),"401","401");
                        objectToBeSaved=initiateAction(jsonData,objectToBeSaved,initiatorActions,entityType);
                        break;
                    case "APPROVE":
                        if (!operator.getRoles().stream().findAny().get().getUserAuthorisationType().equalsIgnoreCase("AUTHORIZER"))
                            throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE.getValue().replace("{}",entityType.getValue()),"401","401");
                        objectToBeSaved= approveAction(objectToBeSaved, action,operator,entityType);
                        break;
                    case "REJECT":
                        if (!operator.getRoles().stream().findAny().get().getUserAuthorisationType().equalsIgnoreCase("AUTHORIZER"))
                            throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE.getValue().replace("{}",entityType.getValue()),"401","401");
                        objectToBeSaved = rejectAction(objectToBeSaved,action,request,entityType);
                }
                if (objectToBeSaved==null)
                    throw new CMMSException(Errors.REQUEST_TERMINATED.getValue(),"500","500");
                else
                {
                    return objectToBeSaved;
                }



            }catch (IOException e)
            {
                e.printStackTrace();
                throw new CMMSException(e.getMessage(),"500","500");
            }
    }
    public String selectAction(InitiatorActions initiatorActions,AuthorizationAction authorizationAction,
                               AuthorizationRequest request) throws CMMSException {
        if (Arrays.asList(INITIATE_ACTIONS).contains(initiatorActions))
            return "INITIATE";
        else if (Arrays.asList(APPROVE_ACTIONS).contains(authorizationAction))
            return "APPROVE";
        else if (Arrays.asList(REJECT_ACTIONS).contains(authorizationAction))
        {
            if (StringUtils.isEmpty(request.getReason()))
            {
                throw new CMMSException("FAILED: Provide reason for the rejection","400","400");
            }
            return "REJECT";
        }
        else {
            throw new CMMSException(Errors.REQUEST_TERMINATED.getValue()+" No action found","500","500");
//            return null;
        }
    }

//    public  User generateUser(User nibssJson, User objectToBeUpdated, User operator){
//        objectToBeUpdated.setContactDetails(nibssJson.getContactDetails());
//        objectToBeUpdated.setEmailAddress(nibssJson.getEmailAddress());
//        objectToBeUpdated.setName(nibssJson.getName());
//        objectToBeUpdated.setRoles(nibssJson.getRoles());
//        objectToBeUpdated.setPhoneNumber(nibssJson.getPhoneNumber());
////        objectToBeUpdated.setU(new User(operator.getId(),operator.getName(),operator.getEmailAddress()));
//        objectToBeUpdated.setJsonData(null);
//        objectToBeUpdated.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);
//        return objectToBeUpdated;
//    }
//    public  Bank generateBank(Bank bankJson, Bank objectToBeUpdated, User operator){
//        objectToBeUpdated.setName(bankJson.getName());
//        objectToBeUpdated.setClientPassKey(bankJson.getNipBankCode());
//        objectToBeUpdated.setCode(bankJson.getCode());
//        objectToBeUpdated.setUsername(bankJson.getPhoneNumber());
////        objectToBeUpdated.setU(new User(operator.getId(),operator.getName(),operator.getEmailAddress()));
//        objectToBeUpdated.setJsonData(null);
//        objectToBeUpdated.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);
//        return objectToBeUpdated;
//    }

    public AuthorizationTable performAuthorization(AuthorizationRequest request, AuthorizationAction authorizationAction,
                                                   UserDetail userDetail, EntityType entityType) throws CMMSException {
        AuthorizationTable authorizationTableObject;
        User operatorUser =null;
        //get the user logged in
        if (request.getId() !=null)
        {
            operatorUser = userService.get(userDetail.getUserId());
            if (operatorUser == null)
            {
                throw new CMMSException(Errors.UNKNOWN_USER.getValue()+" login and try again","500","500");
            }
        }

        if (authorizationAction == null){
            throw new CMMSException("Please specify the action","500","500");
        }
        request.setAuthorization(authorizationAction);
        //get the user to be updated
        authorizationTableObject = authorizationRepo.findOne(request.getId());
        if (authorizationTableObject == null)
        {
            throw new CMMSException("Invalid Id provided.","500","500");
        }
        selectEntity(authorizationAction, authorizationTableObject, operatorUser);
        authorizationTableObject = actions(null, authorizationTableObject, operatorUser, authorizationAction,null,request,entityType);
          authorizationTableObject = performSave(authorizationTableObject,entityType);
        if (authorizationTableObject == null)
        {
            throw new CMMSException("Failed: Could not authorize.","500","500");
        }
        return authorizationTableObject;
    }

    public void selectEntity(AuthorizationAction authorizationAction, AuthorizationTable authorizationTableObject, User operatorUser) throws CMMSException {
        switch (authorizationTableObject.getEntityType())
        {
            case BANK:
                bankService.authenticate(operatorUser,authorizationAction,null);
                break;
            case BILLER:
                billerService.authenticate(operatorUser,authorizationAction,null);
                break;
            case PRODUCT:
                productService.authenticate(operatorUser,authorizationAction,null,null,true);
                break;
            case PSSP:
                psspService.authenticate(authorizationAction,null,operatorUser);
                break;
            case SUBSCRIBER: //billerService.authenticate(operatorUser,authorizationAction,null);
                break;
            case INDUSTRY:
                industryService.authenticate(operatorUser,authorizationAction,null);
                break;
            case EMANDATE:
                configureEmandateService.authenticate(operatorUser,authorizationAction,null,(EmandateConfig)authorizationTableObject);
                break;
            default:throw new CMMSException("Authentication failed","500","500");
        }
    }

    public Page<AuthorizationTable> selectView(Long id, ViewAction viewAction, Pageable pageable,EntityType entityType)
    {
        switch (viewAction)
        {
            case UNAUTHORIZED:
                return this.getAllPending(entityType,pageable);
            case AUTHORIZED:
                return getAllApproved(entityType,AuthorizationStatus.AUTHORIZED,pageable);
            case REJECTED:
                return getAllRejected(entityType,Arrays.asList(UserAuthorizationService.rejectionStatuses),pageable);
            default:
                return getAllApproved(entityType,AuthorizationStatus.AUTHORIZED,pageable);
        }
    }
    private  Page<AuthorizationTable> getAllPending(EntityType entityType,Pageable pageable){
        return this.authorizationRepo.getAllPending(entityType,Arrays.asList(UserAuthorizationService.pendingActions),pageable);
    }

    private Page<AuthorizationTable> getAllApproved(EntityType entityType,AuthorizationStatus authStatus, Pageable pageable)
    {
        return authorizationRepo.getAllApproved(entityType,authStatus,pageable);
    }

    private  Page<AuthorizationTable> getAllRejected(EntityType entityType,List<AuthorizationStatus> authStatusList, Pageable pageable)
    {
        return authorizationRepo.getAllRejected(entityType,authStatusList,pageable);
    }

    public AuthorizationTable previewUpdate(Long id) throws CMMSException {
        AuthorizationTable objectRetrieved = authorizationRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        return generateApprovedObject(objectRetrieved, null);
    }

}