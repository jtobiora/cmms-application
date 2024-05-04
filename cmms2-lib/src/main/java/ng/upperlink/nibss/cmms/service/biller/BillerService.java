package ng.upperlink.nibss.cmms.service.biller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.BillerRequest;
import ng.upperlink.nibss.cmms.dto.biller.FeesRequest;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.makerchecker.*;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.Industry;
import ng.upperlink.nibss.cmms.model.mandate.Fee;
import ng.upperlink.nibss.cmms.model.pssp.PsspUser;
import ng.upperlink.nibss.cmms.repo.biller.BillerRepo;
import ng.upperlink.nibss.cmms.repo.biller.FeeRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import ng.upperlink.nibss.cmms.service.makerchecker.UserAuthorizationService;
import ng.upperlink.nibss.cmms.service.nibss.NibssUserService;
import ng.upperlink.nibss.cmms.service.pssp.PsspService;
import ng.upperlink.nibss.cmms.service.pssp.PsspUserService;
import ng.upperlink.nibss.cmms.util.AccountLookUp;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
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
import java.util.stream.Collectors;

/**
 * Service Interface for managing Biller.
 */
@Service
@Slf4j
@Transactional
public class BillerService {


    private static Logger LOG = LoggerFactory.getLogger(BillerService.class);

    private BillerRepo billerRepo;
    private UserService userService;
    private BankService bankService;
    private BankUserService bankUserService;
    private NibssUserService nibssUserService;
    private IndustryService industryService;
    private CompanyService companyService;
    private PsspUserService psspUserService;
    private PsspService psspService;
    private FeeRepo feeRepo;
    private OtherAuthorizationService otherAuthorizationService;
    @Value("${nibss-identity-key}")
    private String nibssId;
    private AccountLookUp accountLookUp;

    @Autowired
    public void setPsspUserService(PsspUserService psspUserService) {
        this.psspUserService = psspUserService;
    }

    @Autowired
    public void setPsspService(PsspService psspService) {
        this.psspService = psspService;
    }

    @Autowired
    public void setOtherAuthorizationService(OtherAuthorizationService otherAuthorizationService) {
        this.otherAuthorizationService = otherAuthorizationService;
    }

    @Autowired
    public void setAccountLookUp(AccountLookUp accountLookUp) {
        this.accountLookUp = accountLookUp;
    }

    @Autowired
    public void setFeeRepo(FeeRepo feeRepo) {
        this.feeRepo = feeRepo;
    }

    @Autowired
    public void setIndustryService(IndustryService industryService) {
        this.industryService = industryService;
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
    public void setBillerRepo(BillerRepo billerRepo) {
        this.billerRepo = billerRepo;
    }

    @Autowired
    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Biller save(Biller biller) {
        return billerRepo.save(biller);
    }

    public Page<Biller> getAllActive(boolean activeStatus, Pageable pageable) {
        return billerRepo.getActiveBillers(activeStatus, AuthorizationStatus.CREATION_REJECTED, pageable);
    }

    public List<Biller> getAllActive(boolean activeStatus) {
        return billerRepo.getActiveBillers(activeStatus, AuthorizationStatus.CREATION_REJECTED);
    }

    public Page<Biller> getAllBiller(Pageable pageable) {
        return billerRepo.getAllBillers(pageable);
    }

    public List<Biller> getAllByBillerOwner(boolean activeStatus, UserDetail userDetail) throws CMMSException {
        String billerOwner = getBillerOwner(userDetail);
        return billerRepo.getByBillerOwner(billerOwner, activeStatus, AuthorizationStatus.CREATION_REJECTED);
    }

    public Page<Biller> getAllByBillerOwner(boolean activeStatus, UserDetail userDetail, Pageable pageable) throws CMMSException {
        String billerOwner = getBillerOwner(userDetail);
        return billerRepo.getByBillerOwner(billerOwner, activeStatus, AuthorizationStatus.CREATION_REJECTED, pageable);
    }

    public Biller getByDomainName(String domainName) {
        return billerRepo.getByDomainName(domainName);
    }

//    public long getCountBankByDomainName(String domainName) {
//        return billerRepo.countByDomainName(domainName);
//    }
//
//    public long getCountBankByDomain(String domainName, Long id) {
//        return billerRepo.countByDomainName(domainName, id);
//    }

    public long countAccountNumber(String accountNumber) {

        return billerRepo.countAccountNumber(accountNumber);
    }

    public long countBankAsBillerId(Long bankAsBiller) {
        return billerRepo.countByBankAsBiller(bankAsBiller);
    }

    public Biller getBillerById(Long id) {
        return billerRepo.getById(id);
    }

    public long getCountOfSameRCNumber(String rcNumber, Long id) {
        if (id == null) {
            return companyService.countOfSameRCNumber(rcNumber);
        }
        return companyService.countOfSameRCNumber(rcNumber, id);
    }

    public Biller getBillerByBankAsBiller(Long bankAsBillerId) {
        return billerRepo.getBillerByBankAsBiller(bankAsBillerId);
    }

    public Biller getBillerByOwner(String billerOwner) {
        return billerRepo.getBillerByBillerOwner(billerOwner);
    }

    public ResponseEntity<?> toggleInit(Long id, UserDetail userDetail) throws CMMSException {
        User operatorUser = userService.get(userDetail.getUserId());
        if (operatorUser == null) {
            throw new CMMSException(Errors.UNKNOWN_USER.getValue(), "404", "404");
        }
        authenticate(operatorUser, null, InitiatorActions.TOGGLE);

        Biller billerToBeToggled = this.getBillerById(id);
        if (billerToBeToggled == null) {
            throw new CMMSException("Toggling failed, Biller not found", "404", "404");
        }
        billerToBeToggled = (Biller) otherAuthorizationService.actions(null, billerToBeToggled, operatorUser, null, InitiatorActions.TOGGLE, null, EntityType.BILLER);


        return ResponseEntity.ok(billerToBeToggled);
    }

//    public Biller approveToggle(Biller billerToBeApprove, boolean isEmandate, User operatorUser) {
//        return isEmandate ? toggleEmandate(billerToBeApprove, operatorUser) : toggleBiller(billerToBeApprove, operatorUser);
//    }

//    public Biller toggleBiller(Biller billerToBeToggled, User user) {
//        boolean activated = billerToBeToggled.isActivated();
//        billerToBeToggled.setUpdatedBy(user);
//        billerToBeToggled.setActivated(!activated);
//        return save(billerToBeToggled);
//
//
//    }

//    public Biller toggleEmandate(Biller billerToBeToggled, User user) {
//        boolean emandateEnabled = billerToBeToggled.isEmandateEnabled();
//        billerToBeToggled.setUpdatedBy(user);
//        billerToBeToggled.setEmandateEnabled(!emandateEnabled);
//        return save(billerToBeToggled);
//
//    }

    public void validate(BillerRequest request, boolean isUpdate, Long id) throws CMMSException {

        long rcNumberCount = 0;
        long accountNumberCount = 0;
        if (isUpdate) {
            if (id == null) {
                throw new CMMSException("Biller id is not provided", "401", "401");
            }
            rcNumberCount = getCountOfSameRCNumber(request.getRcNumber(), request.getId());
        } else {

            if (request.getRcNumber() == null || request.getRcNumber().isEmpty()) {
                throw new CMMSException("Biller RcNumber is not provided", "401", "401");
            }

            rcNumberCount = companyService.countOfSameRCNumber(request.getRcNumber());

            if (request.getAccountNumber() == null || request.getAccountNumber().isEmpty()) {
                throw new CMMSException("Biller bank account number is not provided", "401", "401");
            }
            accountNumberCount = countAccountNumber(request.getAccountNumber());
        }
        if (rcNumberCount > 0) {
            throw new CMMSException("This RcNumber '" + request.getRcNumber() + "' already exist", "401", "401");
        }
        if (accountNumberCount > 0) {
            throw new CMMSException("This account number '" + request.getAccountNumber() + "' already exist", "401", "401");
        }

    }

    public Biller generate(Biller newBiller, Biller existingBiller, BillerRequest request, User operator, boolean isUpdate) throws CMMSException {
        newBiller = generateUpdate(newBiller, request);
        if (isUpdate) {
//            newBiller.setUpdatedAt(new Date());
            newBiller.setUpdatedBy(setUser(operator));
            String jsonData = null;
            try {
                jsonData = JsonBuilder.generateJson(newBiller);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                log.error("Error track --",e);
                throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(), "500", "500");
            }
            return (Biller) otherAuthorizationService.actions(jsonData, existingBiller, operator, null, InitiatorActions.UPDATE, null, EntityType.BILLER);

        } else {

            newBiller.setActivated(false);
            newBiller.setAccountName(request.getAccountName());
            newBiller.setAccountNumber(request.getAccountNumber());
//            newBiller.setEmandateEnabled(true);
//            newBiller.setCreatedAt(new Date());
            generateApiKey(newBiller, request);
//            generatePassKey(newBiller, request);
            if (request.isSelfBiller())// && operator.getEntityType().equals(UserType.BANK))
            {
                createSeflAsBiller(newBiller, request, operator);

            }
            newBiller = setBillerOwner(newBiller, request, operator);
            newBiller.setCreatedBy(setUser(operator));
            return (Biller) otherAuthorizationService.actions(null, newBiller, operator, null, InitiatorActions.CREATE, null, EntityType.BILLER);
        }
    }

//    public void generatePassKey(Biller newBiller, BillerRequest request) {
//        newBiller.setBillerPassKey(request.getRcNumber() + EncyptionUtil.generateString(20, true, true));
//    }

    public void generateApiKey(Biller newBiller, BillerRequest request) {
        newBiller.setApiKey(request.getRcNumber() + EncyptionUtil.generateString(20, true, true));
    }

    public User setUser(User operator) {
        return new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.getUserType());
    }

    public Bank setBank(Bank billerBank) {
        return new Bank(billerBank.getId(), billerBank.getCode(), billerBank.getName());
    }

    public Biller setBillerOwner(Biller newBiller, BillerRequest request, User operator) {
        switch (operator.getUserType()) {
            case BANK:
                BankUser bankUser = (BankUser)operator;
                newBiller.setBillerOwner(bankUser.getUserBank().getApiKey());
                newBiller.setOwnerType(BillerOwner.BANK);
                break;
            case NIBSS:
                newBiller.setBillerOwner(nibssId);
                newBiller.setOwnerType(BillerOwner.NIBSS);
                break;
            case PSSP:
                PsspUser psspUser = (PsspUser) operator;
                newBiller.setBillerOwner(psspUser.getPssp().getApiKey());
                newBiller.setOwnerType(BillerOwner.PSSP);
                break;
        }
        return newBiller;
    }

    public Biller createSeflAsBiller(Biller newBiller, BillerRequest request, User operator) throws CMMSException {
        switch (operator.getUserType()) {
            case BANK:
                newBiller.setBankAsBiller(newBiller.getBank());
                break;
            case NIBSS:
                throw new CMMSException("Failed: NIBSS can only recruit other billers", "401", "401");
//                break;
            case PSSP:
                throw new CMMSException("Failed: PSSP can only recruit other billers", "401", "401");
//                break;
        }
        return newBiller;
    }

    public Biller generateUpdate(Biller newBiller, BillerRequest request) {
        newBiller.setDescription(request.getDescription());
        newBiller.setName(request.getName());
//        newBiller.setDomainName(request.getDomainName());
//        newBiller.setBillerUserName(request.getUsername());
        newBiller.setRcNumber(request.getRcNumber());
        newBiller.setAccountName(request.getAccountName());
        newBiller.setAccountNumber(request.getAccountNumber());
        newBiller.setBvn(request.getBvn());
        return newBiller;
    }

    public Industry setIndustry(Industry industry) {

        return new Industry(industry.getId(), industry.getName(), industry.getDescription());
    }

    public Biller generateApproved(Biller billerToBeUpdated, Biller froJson, User operator) {
        billerToBeUpdated.setDescription(froJson.getDescription());
        billerToBeUpdated.setName(froJson.getName());
        billerToBeUpdated.setUpdatedBy(setUser(froJson.getUpdatedBy()));
        billerToBeUpdated.setApprovedBy(setUser(operator));
//        billerToBeUpdated.setAccountName(froJson.getAccountName());
//        billerToBeUpdated.setAccountNumber(froJson.getAccountNumber());
        billerToBeUpdated.setRcNumber(froJson.getRcNumber());
        billerToBeUpdated.setBvn(froJson.getBvn());

        return billerToBeUpdated;
    }

    public void validateSelfAsBiller(Long creatorAsBillerId, BillerOwner billerOwner) throws CMMSException {
        long countBankAsBillerId = 0;
        long countNIBSSBiller = 0;
        long countPSSPBiller = 0;
        switch (billerOwner) {
            case BANK:
                if (creatorAsBillerId == null || creatorAsBillerId == 0) {
                    throw new CMMSException("Could not verify if your bank is already a biller : Bank Id is not provided ", "401", "401");
                }

                countBankAsBillerId = billerRepo.countByBankAsBiller(creatorAsBillerId);

                if (countBankAsBillerId > 0) {
                    throw new CMMSException("Not permitted to create another biller for this bank : Your bank is already existing as a biller ", "401", "401");
                }

                break;
            case NIBSS:
                countNIBSSBiller = billerRepo.countBillersByOwner(BillerOwner.NIBSS.getValue());
                if (countNIBSSBiller > 0) {
                    throw new CMMSException("Not permitted to create another self biller. " + billerOwner.getValue() + " is already existing as a biller", "401", "401");
                }
                break;
            case PSSP:
                countPSSPBiller = billerRepo.countBillersByOwner(BillerOwner.NIBSS.getValue());
                if (countPSSPBiller > 0) {
                    throw new CMMSException("Not permitted to create another self biller. " + billerOwner.getValue() + " is already existing as a biller", "401", "401");
                }
                break;
        }

    }

    public Biller getBillerByUsername(String username) {
        return billerRepo.getBillerByUserName(username);
    }

    public Biller getBillerByRcNumber(String rcNumber) {
        return billerRepo.getBillerByRcNumber(rcNumber);
    }

    public ResponseEntity<?> setup(BillerRequest billerRequest, boolean isUpdate, UserDetail userDetail,
                                   AuthorizationAction action, InitiatorActions initiatorActions) throws CMMSException {

        Collection<Role> roles = null;

        User operator = userService.get(userDetail.getUserId());
        if (operator == null) {
            throw new CMMSException(Errors.UNKNOWN_USER.getValue(), "401", "401");
        }

        authenticate(operator, action, initiatorActions);

        this.validate(billerRequest, isUpdate, billerRequest.getId());
        Industry industry = industryService.getOne(billerRequest.getIndustryId());
        if (industry == null) {
            throw new CMMSException("Failed: Industry not found", "401", "401");
        }
        Biller newBiller = new Biller();
        newBiller.setIndustry(industry);
        if (isUpdate) {
            Biller existingBiller = getBillerById(billerRequest.getId());

            if (existingBiller == null) {
                throw new CMMSException(Errors.DATA_IS_NULL.getValue().replace("{}", "Biller to be updated "), "401", "401");
            }
            existingBiller = generate(newBiller, existingBiller, billerRequest, operator, true);
            return ResponseEntity.ok(this.save(existingBiller));

        } else {
            newBiller = setBillerBank(billerRequest, operator, newBiller);
            newBiller = generate(newBiller, null, billerRequest, operator, false);
            return ResponseEntity.ok(save(newBiller));
        }

    }

    public Biller setBillerBank(BillerRequest billerRequest, User operator, Biller newBiller) throws CMMSException {
        Bank billerBank;
        switch (operator.getUserType()) {
            case NIBSS:
                billerBank = bankService.getByBankId(billerRequest.getBankId());
                if (billerBank == null) {
                    throw new CMMSException("No bank is provided to tie the biller", "401", "401");
                }
                newBiller.setBank(setBank(billerBank));
                if (billerRequest.isSelfBiller()) {
                    validateSelfAsBiller(null, BillerOwner.NIBSS);
                }
                break;
            case BANK:
                BankUser operatorUserBank = (BankUser) operator;
                billerBank = operatorUserBank.getUserBank();
                if (billerBank == null) {
                    throw new CMMSException("No bank is retrieved from the logged in user to tie to this biller ", "401", "401");
                }
                newBiller.setBank(setBank(billerBank));
                if (billerRequest.isSelfBiller()) {
                    validateSelfAsBiller(billerBank.getId(), BillerOwner.BANK);
                }
                break;
            case PSSP:
                PsspUser psspUser = (PsspUser) operator;
                billerBank = bankService.getByBankId(billerRequest.getBankId());
                Long psspId = psspUser.getPssp().getId();
                if (billerBank == null) {
                    throw new CMMSException("No bank is provided to tie the biller", "404", "400");
                }
                newBiller.setBank(setBank(billerBank));
                if (billerRequest.isSelfBiller()) {
                    validateSelfAsBiller(psspId, BillerOwner.PSSP);
                }
                break;
            default: {
                throw new CMMSException("Biller setup failed: ", "500", "500");
            }
        }
        return newBiller;
    }

    public void authenticate(User operator, AuthorizationAction action, InitiatorActions initiatorActions) throws CMMSException {
        Collection<Role> roles;
        roles = operator.getRoles();
        if (roles == null) {
            throw new CMMSException(Errors.NOT_PERMITTED.getValue(), "401", "401");
        }
        Role operatorRole = roles.stream().findAny().get();

        RoleName[] initiatorRoles = {RoleName.BANK_ADMIN_INITIATOR, RoleName.NIBSS_INITIATOR, RoleName.PSSP_ADMIN_INITIATOR};
        RoleName[] authorizerRoles = {RoleName.BANK_ADMIN_AUTHORIZER, RoleName.NIBSS_AUTHORIZER, RoleName.PSSP_ADMIN_AUTHORIZER};

        if (!Arrays.asList(initiatorRoles).contains(operatorRole.getName()) && !Arrays.asList(authorizerRoles).contains(operatorRole.getName())) {
            throw new CMMSException(Errors.NOT_PERMIT.getValue().replace("{}", "Biller"), "401", "401");
        }
        if (Arrays.asList(UserAuthorizationService.INITIATE_ACTIONS).contains(initiatorActions) && !Arrays.asList(initiatorRoles).contains(operatorRole.getName())) {
            throw new CMMSException(Errors.NOT_PERMITTED.getValue().replace("{}", "Biller"), "401", "401");
        }
        if (Arrays.asList(UserAuthorizationService.APPROVE_ACTIONS).contains(action) ||
                Arrays.asList(UserAuthorizationService.REJECT_ACTIONS).contains(action)) {
            if (!Arrays.asList(authorizerRoles).contains(operatorRole.getName())) {
                throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE.getValue().replace("{}", "Biller"), "401", "401");
            }
        }
    }

    public Map<String, String> validateUserPermissions(UserDetail userDetail, String[] permittedRoles) {
        Map<String, String> mapValidator = new HashMap<>();
        String message = null;
        User operatorUser = userService.get(userDetail.getUserId());

        if (operatorUser == null) {
            mapValidator.put("message", Errors.UNKNOWN_USER.getValue());
            return mapValidator;
        }

        String role = operatorUser.getRoles().stream().map(r -> r.getName().getValue()).collect(Collectors.toList()).get(0);

        if (!Arrays.asList(permittedRoles).contains(role)) {
            mapValidator.put("message", Errors.NOT_PERMITTED.getValue());
            return mapValidator;
        }

        mapValidator.put("message", "");

        return mapValidator;
    }

    // Fee configuration when setting up billers
    public Fee saveFeeSetUpForBillers(FeesRequest request, Fee fees) {

        if (request != null) {
            fees.setBillerDebitAccountNumber(request.getBillerDebitAccountNumber());
            fees.setDebitAtTransactionTime(request.isDebitAtTransactionTime());
            fees.setFeeBearer(FeeBearer.find(request.getFeeBearer()));
            fees.setSplitType(SplitType.find(request.getSplitType()));
            fees.setMarkUpFee(request.getMarkUpFee());
            fees.setPercentageAmount(request.getPercentageAmount());

            return feeRepo.save(fees);
        }

        return null;
    }

    public ResponseEntity<?> loggedUser(UserDetail userDetail) throws CMMSException {
        List<Biller> billers = null;
        switch (UserType.valueOf(userDetail.getUserType())) {
            case BANK:
                BankUser operatorBankUser = bankUserService.getById(userDetail.getUserId());
                String billerOwner = operatorBankUser.getUserBank().getApiKey();
                billers = getAllByBillerOwner(true, userDetail);
                break;
            case NIBSS:
                billers = getAllByBillerOwner(true, userDetail);
                break;
        }
        if (billers.isEmpty())
            return ResponseEntity.status(404).body(new ErrorDetails("Could not retrieve the billers"));

        return ResponseEntity.ok(billers);
    }


    public Page<Biller> selectView(UserDetail userDetail, ViewAction viewAction, Pageable pageable) throws CMMSException {
        String billerOwner = getBillerOwner(userDetail);
        switch (viewAction) {
            case UNAUTHORIZED:
                return this.getAllPending(billerOwner, pageable);
            case AUTHORIZED:
                return getAllApproved(billerOwner, AuthorizationStatus.AUTHORIZED, pageable);
            case REJECTED:
                return getAllRejected(billerOwner, Arrays.asList(UserAuthorizationService.rejectionStatuses), pageable);
            default:
                return getAllApproved(billerOwner, AuthorizationStatus.AUTHORIZED, pageable);
        }
    }

    public Page<Biller> selectViewConfig(UserDetail userDetail, ViewAction viewAction, Pageable pageable) throws CMMSException {
        String billerOwner = getBillerOwner(userDetail);
        switch (viewAction) {
            case UNAUTHORIZED:
                return this.getAllPendingConfig(billerOwner, pageable);
            case AUTHORIZED:
                return getAllApprovedConfig(billerOwner, AuthorizationStatus.AUTHORIZED, pageable);
            case REJECTED:
                return getAllRejectedConfig(billerOwner, Arrays.asList(UserAuthorizationService.rejectionStatuses), pageable);
            default:
                return getAllApprovedConfig(billerOwner, AuthorizationStatus.AUTHORIZED, pageable);
        }
    }

    private String getBillerOwner(UserDetail userDetail) throws CMMSException {
        User operator = userService.get(userDetail.getUserId());
        String billerOwner = null;
        if (operator == null) {
            throw new CMMSException("Please Login and try again", "401", "401");
        }
        switch (operator.getUserType()) {
            case NIBSS:
                billerOwner = nibssId;
                break;
            case BANK:
                BankUser bankUser = (BankUser) operator;
                billerOwner = bankUser.getUserBank().getApiKey();
                break;
            case PSSP:
                PsspUser psspUser = (PsspUser) operator;
                billerOwner = psspUser.getPssp().getApiKey();
                break;
            default:
                throw new CMMSException("Unknown user type found", "400", "400");
        }
        if (billerOwner == null) {
            throw new CMMSException("Biller owner is not found", "404", "404");
        }
        return billerOwner;
    }

    private Page<Biller> getAllPending(String billerOwner, Pageable pageable) {
        return this.billerRepo.getAllPending(billerOwner, Arrays.asList(UserAuthorizationService.pendingActions), pageable);
    }

    private Page<Biller> getAllApproved(String billerOwner, AuthorizationStatus authStatus, Pageable pageable) {
        return billerRepo.getAllApproved(billerOwner, authStatus, pageable);
    }

    private Page<Biller> getAllRejected(String billerOwner, List<AuthorizationStatus> authStatusList, Pageable pageable) {
        return billerRepo.getAllRejected(billerOwner, authStatusList, pageable);
    }

    private Page<Biller> getAllPendingConfig(String billerOwner, Pageable pageable) {
        return this.billerRepo.getAllPendingConfig(billerOwner, Arrays.asList(UserAuthorizationService.pendingActions), pageable);
    }

    private Page<Biller> getAllApprovedConfig(String billerOwner, AuthorizationStatus authStatus, Pageable pageable) {
        return billerRepo.getAllApprovedConfig(billerOwner, authStatus, pageable);
    }

    private Page<Biller> getAllRejectedConfig(String billerOwner, List<AuthorizationStatus> authStatusList, Pageable pageable) {
        return billerRepo.getAllRejectedConfig(billerOwner, authStatusList, pageable);
    }

    public Biller previewUpdate(Long id) throws CMMSException {
        Biller fromJson = billerRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        if (fromJson == null) {
            throw new CMMSException(Errors.NO_UPDATE_REQUEST.getValue(), "404", "404");
        }
        String jsonData = fromJson.getJsonData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Biller jsonUser = mapper.readValue(jsonData, Biller.class);
            return jsonUser;
        } catch (IOException e) {
            log.error("IOException {}",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
        }
    }

    public Object getBillerOwner(String apiKey, BillerOwner billerOwner) {
        switch (billerOwner) {
            case BANK:
                return bankService.getByApiKey(apiKey);
            case PSSP:
                return psspService.getByApiKey(apiKey);
            default:
                return null;
        }
    }

    public Page<Biller> getAllBillers(Pageable pageable) {
        return billerRepo.getAllBillers(pageable);
    }

    public List<Biller> getAllBillers() {
        return billerRepo.getAllBillers();
    }

    long countAllByBillerId(String billerOwner) {
        return billerRepo.countBillersByOwner(billerOwner);
    }

    long countAllByBillerIdAndStatus(boolean status, String billerOwner) {
        return billerRepo.countAllByBillerIdAndStatus(status, billerOwner);
    }

    long countAll(Long id) {
        return billerRepo.countAll(id);
    }

    long countAllByStatus(boolean status) {
        return billerRepo.countAllByStatus(status);
    }

    public List<Biller> getAllByBillerOwner(UserDetail userDetail) throws CMMSException {
        String billerOwner = getBillerOwner(userDetail);
        return billerRepo.getByBillerOwner(billerOwner, AuthorizationStatus.CREATION_REJECTED);
    }

    public Page<Biller> getAllByBillerOwner(UserDetail userDetail, Pageable pageable) throws CMMSException {
        String billerOwner = getBillerOwner(userDetail);
        return billerRepo.getByBillerOwner(billerOwner,AuthorizationStatus.CREATION_REJECTED, pageable);
    }
}
