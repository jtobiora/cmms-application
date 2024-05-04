package ng.upperlink.nibss.cmms.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ng.upperlink.nibss.cmms.dto.SubscriberDto;
import ng.upperlink.nibss.cmms.dto.SubscriberRequest;
import ng.upperlink.nibss.cmms.dto.search.PageSearch;
import ng.upperlink.nibss.cmms.embeddables.makerchecker.MakerChecker;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.enums.makerchecker.ApprovalStatus;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.Subscriber;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.repo.SubscriberRepo;
import ng.upperlink.nibss.cmms.util.CommonUtils;
import ng.upperlink.nibss.cmms.util.HibernateProxyTypeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Service
public class SubscriberService {

    private static Logger LOG = LoggerFactory.getLogger(SubscriberService.class);

    private SubscriberRepo subscriberRepo;

    private UserService userService;


    UserType userType = UserType.NIBSS;

    private static final String AGTMGR = "AGTMGR";

    @Autowired
    public void setSubscriberRepoRepo(SubscriberRepo agentManagerRepo) {
        this.subscriberRepo = agentManagerRepo;
    }
   /* @Autowired
    public void setAgentManagerInstitutionService(AgentManagerInstitutionService agentManagerInstitutionService) {
        this.agentManagerInstitutionService = agentManagerInstitutionService;
    }*/

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Page<Subscriber> get(Pageable pageable){
        return subscriberRepo.getAll(userType, pageable);
    }
    public Page<Subscriber> getAllByAnyKey(String anyKey, Pageable pageable){return subscriberRepo.getAllByAnyKey(anyKey, pageable);}

    public List<Subscriber> getAllActivated(){
        return subscriberRepo.getAllActivated(userType);
    }

    public Subscriber get(Long id){
        return subscriberRepo.get(id,userType);
    }

    public Subscriber getByUserId(Long userId){
        return subscriberRepo.getByUser(userId);
    }
    public List<String> getAllActiveAuthorizerEmailAddressByInstitutionCode(String institutionCode){
        return subscriberRepo.getAllActiveAuthorizerEmailAddressByInstitutionCode(institutionCode);
    }
    public Subscriber save(Subscriber agentManager){
        return subscriberRepo.save(agentManager);
    }

    public void validate(SubscriberRequest request, boolean isUpdate, Long id) throws CMMSException {

        Subscriber agentManager = null;
        if (isUpdate){
            if (id == null)
            {
                throw new CMMSException(Errors.UNKNOWN_USER.getValue(),"404","404");
            }

//            agentManager = getByNameAndNotId(roles.getName(), id);
        }else {
//            existingRole = getByName(roles.getName());
        }
       validate((SubscriberRequest) userService.getUserRequest(request), isUpdate, request.getUserId());
    }

    public Subscriber generateForApproval(Subscriber agentManager){

        //set the json data for approval (this is done when creating and updating)
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        b.setDateFormat("MMM dd, yyyy HH:mm:ss aa");
        Gson gson = b.create();

       // User user = agentManager.getUser();
       // user.getRoles().setPrivileges(null);

        agentManager.getMakerChecker().setUnapprovedData(gson.toJson(agentManager));
        agentManager.getMakerChecker().setApprovalStatus(null); //--PENDING APPROVAL

        return agentManager;
    }

    public Subscriber generateForApproval(Subscriber agentManager, Subscriber approvedAgentManager, Boolean approvalStatus){

        //set the json data for approval (this is done when creating and updating)
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        Gson gson = b.create();

        agentManager.getMakerChecker().setUnapprovedData(gson.toJson(approvedAgentManager));
        agentManager.getMakerChecker().setApprovalStatus(approvalStatus); //--PENDING APPROVAL

        return agentManager;
    }

    public Subscriber generateForApproval(Subscriber agentManager, Subscriber unapprovedAgentManager){

        //set the json data for approval (this is done when creating and updating)
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        Gson gson = b.create();

        if (agentManager.getMakerChecker() == null){
            agentManager.setMakerChecker(new MakerChecker());
        }
        agentManager.getMakerChecker().setUnapprovedData(gson.toJson(unapprovedAgentManager));
        agentManager.getMakerChecker().setApprovalStatus(null); //--PENDING APPROVAL

        return agentManager;
    }

    public Subscriber generate(Subscriber agentManager, SubscriberRequest request, User actualUser, User user, boolean isUpdate, UserType userType){

        agentManager.setCode(request.getCode());
        agentManager.setAdditionalInfo1(request.getAdditionalInfo1());
        agentManager.setAdditionalInfo2(request.getAdditionalInfo2());
        agentManager.setBankAccountNumber(request.getBankAccountNumber());
        agentManager.setBankCode(request.getBankCode());

        agentManager.setAutoGeneratedCode(request.isAutoGeneratedCode());

        agentManager.setMakerCheckerType(MakerCheckerType.find(request.getUserAuthorisationType()));
        User agentMgrUser = isUpdate ? actualUser : new User();
        //agentMgrUser = userService.generateAuthRequest(agentMgrUser, userService.getUserRequest(request), isUpdate, entityType, null);
        agentManager.setUser(agentMgrUser);

        if(isUpdate) {
            agentManager.getMakerChecker().setUnapprovedData("");
            agentManager.getMakerChecker().setApprovalStatus(null);
            agentManager.getMakerChecker().setApprovedBy(null);
            agentManager.getMakerChecker().setApprovalReason(null);
            agentManager.getMakerChecker().setApprovalDate(null);
            agentManager.setUpdatedBy(new User(user.getId(),user.getName(), user.getEmailAddress(), user.isActivated(), user.getUserType()));

        } else {
            agentManager.setCreatedBy(new User(user.getId(),user.getName(), user.getEmailAddress(), user.isActivated(), user.getUserType()));
            agentManager.setUpdatedBy(new User(user.getId(),user.getName(), user.getEmailAddress(), user.isActivated(), user.getUserType()));
        }

        return agentManager;
    }


    public Subscriber generate(Subscriber agentManager, String nibssInJson, User user, Boolean approvalStatus, String reason){

        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        b.setDateFormat("MMM dd, yyyy");
        Gson gson = b.create();

        Subscriber newAgentManager = gson.fromJson(nibssInJson, Subscriber.class);

        if (approvalStatus == Boolean.FALSE){
            //Disapproved
            agentManager.getMakerChecker().setApprovedBy(new User(user.getId(),user.getName(),user.getEmailAddress(), user.isActivated(), user.getUserType()));
            agentManager.getMakerChecker().setApprovalReason(reason);
            agentManager.getMakerChecker().setApprovalStatus(approvalStatus);
            agentManager.getMakerChecker().setApprovalDate(new Date());

            newAgentManager.getMakerChecker().setApprovedBy(new User(user.getId(),user.getName(), user.getEmailAddress(),  user.isActivated(), user.getUserType()));
            newAgentManager.getMakerChecker().setApprovalReason(reason);
            newAgentManager.getMakerChecker().setApprovalStatus(approvalStatus);
            newAgentManager.getMakerChecker().setApprovalDate(null);
            generateForApproval(agentManager, newAgentManager, approvalStatus);
        }

        if (approvalStatus == Boolean.TRUE) {
            //Approved
            agentManager.getMakerChecker().setApprovedBy(new User(user.getId(),user.getName(),  user.getEmailAddress(), user.isActivated(), user.getUserType()));
            agentManager.getMakerChecker().setApprovalStatus(approvalStatus);
            agentManager.getMakerChecker().setApprovalDate(new Date());

            newAgentManager.getMakerChecker().setApprovedBy(new User(user.getId(),user.getName(), user.getEmailAddress(), user.isActivated(), user.getUserType()));
            newAgentManager.getMakerChecker().setApprovalStatus(approvalStatus);
            newAgentManager.getMakerChecker().setApprovalDate(null);
            generateForApproval(agentManager, newAgentManager, approvalStatus);

            agentManager.setCode(newAgentManager.getCode());
            agentManager.setMakerCheckerType(newAgentManager.getMakerCheckerType());
            agentManager.setUser(userService.generate(agentManager.getUser(), newAgentManager.getUser()));
        }

        if (approvalStatus == null){
            //PENDING -- nothing should be done
        }

        return agentManager;
    }

    public Page<Subscriber> getJsonData(String approvalStatus, PageSearch pageSearch){

        ApprovalStatus status = ApprovalStatus.find(approvalStatus);
        Pageable pageable = new PageRequest(pageSearch.getPageNumber(), pageSearch.getPageSize());
        if (status == ApprovalStatus.APPROVED){
            if (!pageSearch.isParamEmpty()) {
                return subscriberRepo.getApprovedOrDisapprovedData(true, pageSearch.getParam(), pageable);
            }
            return subscriberRepo.getApprovedOrDisapprovedData(true,pageable);
        }

        if (status == ApprovalStatus.DISAPPROVED){
            if (!pageSearch.isParamEmpty()) {
                return subscriberRepo.getApprovedOrDisapprovedData(false, pageSearch.getParam(), pageable);
            }
            return subscriberRepo.getApprovedOrDisapprovedData(false,pageable);
        }

        if (status == ApprovalStatus.PENDING){
            if (!pageSearch.isParamEmpty()) {
                return subscriberRepo.getUnapprovedData(pageSearch.getParam(), pageable);
            }
            return subscriberRepo.getUnapprovedData(pageable);
        }

        return subscriberRepo.getAllJsonData(pageable);
    }

    public Subscriber toggle(Long id, User user){

        Subscriber agentManager = get(id);
        if (agentManager == null){
            return new Subscriber();
        }

        boolean activated = agentManager.getUser().isActivated();
        agentManager.getUser().setActivated(!activated);
        agentManager.setUpdatedBy(user);
        agentManager = save(agentManager);

        return agentManager;
    }

    public Subscriber getByCode(@NotNull(message = "code is required") String code){
        return subscriberRepo.getByCode(code);
    }

    public long count() {
        synchronized (new Object()) {
            return subscriberRepo.count();
        }
    }

    public Long getIdFromUserId(long userId) {
        synchronized (new Object()) {
            return subscriberRepo.findIdByUserId(userId);
        }
    }

    public String getInstitutionCodeFromUserId(long userId) {
        synchronized (new Object()) {
            return subscriberRepo.findInstitutionCodeByUserId(userId);
        }
    }

    public Long getInstitutionIdFromUserId(long userId) {
        synchronized (new Object()) {
            return subscriberRepo.findInstitutionIdByUserId(userId);
        }
    }


    public Page<SubscriberDto> findAllStrippedDown(Pageable pageable) {
        synchronized (new Object()) {
           return null;// subscriberRepo.findAllStrippedDown(pageable);
        }
    }

    public Page<SubscriberDto> findByDateRange(Date startDate, Date endDate, Pageable pageable) {
        synchronized (new Object()) {
            return  null;//subscriberRepo.findByDateRange(startDate, endDate, pageable);
        }
    }


    public List<SubscriberDto> findByDateRange(Date startDate, Date endDate) {
        synchronized (new Object()) {
            return null;//subscriberRepo.findByDateRange(startDate, endDate);
        }
    }
    public Subscriber getUnapprovedById(Long id){
        return subscriberRepo.getUnapprovedById(id);
    }
    public String generateCode(){
        return CommonUtils.generateCode(subscriberRepo.getAutoGeneratedCodes(), subscriberRepo.getAllCodes(), UserType.BANK, false);
    }

    public long getCountOfSameCode(String code, Long id){
        if (id == null){
            return subscriberRepo.countOfSameCode(code);
        }
        return subscriberRepo.countOfSameCode(code, id);
    }
     public long countOfSameCode(String code){
        return subscriberRepo.countOfSameCode(code);
    }

    public List<String> getAllActiveAuthorizerEmailAddress(){
        return subscriberRepo.getAllActiveAuthorizerEmailAddress();
    }

   /* public List<AgentManagerResponse> getAllByAgentManagerInstitution(Long id){
        return subscriberRepo.getAllByAgentManagerInstitution(id);
    }*/

   /* public List<Subscriber> getAllAgentManager(){
        return subscriberRepo.getAll();
    }*/

   /* public Subscriber getAnAgentManagerByInstitutionName(String name){
        List<Subscriber> agentManagers = subscriberRepo.getAgentManagerByAgentManagerInstitutionName(name, new PageRequest(0, 1));

        int count = agentManagers == null ? 0 : agentManagers.size();
        if (count < 1){
            return null;
        }
        return agentManagers.get(0);
    }*/

}
