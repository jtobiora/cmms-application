package ng.upperlink.nibss.cmms.service.biller;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.IndustryRequest;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.makerchecker.*;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.biller.Industry;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.repo.biller.IndustryRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import ng.upperlink.nibss.cmms.service.makerchecker.UserAuthorizationService;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
@Slf4j
@Service
@Transactional
public class IndustryService {

    private UserService userService;
    private IndustryRepo industryRepo;
    private OtherAuthorizationService otherAuthorizationService;

    @Autowired
    public void setOtherAuthorizationService(OtherAuthorizationService otherAuthorizationService) {
        this.otherAuthorizationService = otherAuthorizationService;
    }

    @Autowired
    public void setIndustryRepo(IndustryRepo industryRepo) {
        this.industryRepo = industryRepo;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Industry save(Industry industry) {
        return industryRepo.save(industry);
    }

    public List<Industry> getAllActivated() {
        return industryRepo.findAll();
    }

    public Page<Industry> getAllActivated(Pageable pageable) {
        return industryRepo.getAllIndustry(AuthorizationStatus.CREATION_REJECTED, pageable);
    }

    public long getCountIndustryByName(String name) {
        return industryRepo.countByName(name);
    }

    public long getCountIndustryByName(String name, Long id) {
        return industryRepo.countByName(name, id);
    }

    public Industry getOne(Long id) {
        return industryRepo.getById(id);
    }

    public Industry findPartial(Long id) {
        return industryRepo.findPartial(id);
    }

    public Industry setUp(IndustryRequest industryRequest, UserDetail userDetail, boolean isUpdate,
                          AuthorizationAction action, InitiatorActions initiatorActions) throws CMMSException {

        User operator = userService.get(userDetail.getUserId());
        authenticate(operator, action, initiatorActions);
        if (operator == null) {
            throw new CMMSException("Operating user is null: Login and try again", "404", "404");

        }
        //make sure that the bank code is not null and doesn't already exist
        Industry newIndustry = new Industry();
        if (isUpdate) {
            if (industryRequest.getId() == null) {
                throw new CMMSException("Industry id is not provided", "401", "401");
            }
            Industry existingIndustry = industryRepo.getById(industryRequest.getId());
            if (existingIndustry == null) {
                throw new CMMSException("No Industry available for update", "404", "404");
            }
            validate(industryRequest, true, existingIndustry.getId());
            existingIndustry = generate(newIndustry, existingIndustry, industryRequest, operator, true);
            return save(existingIndustry);
        } else {
            validate(industryRequest, false, industryRequest.getId());
            newIndustry = generate(newIndustry, null, industryRequest, operator, false);
            return save(newIndustry);
        }
    }

    public void authenticate(User operator, AuthorizationAction action, InitiatorActions initiatorActions) throws CMMSException {
        if (operator == null) {
            throw new CMMSException("Please login and try again", "401", "401");
        }
        Collection<Role> roles = operator.getRoles();
        if (roles == null) {
            throw new CMMSException("You have no role", "401", "401");
        }

        Role operatorRole = roles.stream().findAny().get();

        if (initiatorActions != null && !operatorRole.getName().equals(RoleName.NIBSS_INITIATOR)) {
            throw new CMMSException(Errors.NOT_PERMITTED.getValue() + " Only " + RoleName.NIBSS_INITIATOR.getValue(), "401", "401");
        }
        if (action != null && !operatorRole.getName().equals(RoleName.NIBSS_AUTHORIZER)) {
            throw new CMMSException(Errors.NOT_PERMITTED.getValue() + " Only " + RoleName.NIBSS_AUTHORIZER.getValue(), "401", "401");
        }
        authorizeUser(operator, action, initiatorActions);
    }

    private void authorizeUser(User operator, AuthorizationAction action, InitiatorActions initiatorActions) throws CMMSException {
        Role operatorRole = operator.getRoles().stream().findAny().get();
        if (Arrays.asList(UserAuthorizationService.INITIATE_ACTIONS).contains(initiatorActions) && !operatorRole.getName().equals(RoleName.NIBSS_INITIATOR)) {
            throw new CMMSException(Errors.NOT_PERMITTED_TO_CREATE.getValue(), "401", "401");
        }
        if (Arrays.asList(UserAuthorizationService.APPROVE_ACTIONS).contains(action) || Arrays.asList(UserAuthorizationService.REJECT_ACTIONS).contains(action)) {
            if (!operatorRole.getName().equals(RoleName.NIBSS_AUTHORIZER)) {
                throw new CMMSException(Errors.NOT_PERMITTED_TO_AUTHORIZE.getValue(), "401", "401");
            }
        }
    }

    public void validate(IndustryRequest request, boolean isUpdate, Long id) throws CMMSException {
        long count = 0L;
        if (isUpdate) {

            count = this.getCountIndustryByName(request.getName(), request.getId());
        } else {
            count = this.getCountIndustryByName(request.getName());
        }
        if (count > 0L) {
            throw new CMMSException("Industry name '" + request.getName() + "' already exist", "401", "401");
        }
    }

    public String delete(Long id) {
        industryRepo.delete(id);
        return "Deleted successfully";
    }

    public Industry generate(Industry newIndustry, Industry existingindustry, IndustryRequest request, User operator, boolean isUpdate) throws CMMSException {
        newIndustry = generateUpdate(newIndustry, request);
        if (isUpdate) {
//            newIndustry.setUpdatedAt(new Date());
            newIndustry.setUpdatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.isActivated(), operator.getUserType()));
            String jsonData = null;
            try {
                jsonData = JsonBuilder.generateJson(newIndustry);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                log.error("Errors --- ",e);
                throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500","500");
            }
            return (Industry) otherAuthorizationService.actions(jsonData, existingindustry, operator, null, InitiatorActions.UPDATE, null, EntityType.INDUSTRY);
        } else {
//            newIndustry.setCreatedAt(new Date());
            newIndustry.setCreatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.isActivated(), operator.getUserType()));
            return (Industry) otherAuthorizationService.actions(null, newIndustry, operator, null, InitiatorActions.CREATE, null, EntityType.INDUSTRY);
        }
    }

    public Industry generateUpdate(Industry industry, IndustryRequest industryRequest) {
        industry.setName(industryRequest.getName());
        industry.setDescription(industryRequest.getDescription());
        return industry;
    }

    public Industry generateApproved(Industry industry, Industry fromJson, User operator) {
        industry.setName(fromJson.getName());
        industry.setDescription(fromJson.getDescription());
        return industry;
    }

    public Page<Industry> selectView(ViewAction viewAction, Pageable pageable) {
        switch (viewAction) {
            case UNAUTHORIZED:
                return this.getAllPending(pageable);
            case AUTHORIZED:
                return getAllApproved(AuthorizationStatus.AUTHORIZED, pageable);
            case REJECTED:
                return getAllRejected(Arrays.asList(UserAuthorizationService.rejectionStatuses), pageable);
            default:
                return getAllApproved(AuthorizationStatus.AUTHORIZED, pageable);
        }
    }

    private Page<Industry> getAllPending(Pageable pageable) {
        return this.industryRepo.getAllPending(Arrays.asList(UserAuthorizationService.pendingActions), pageable);
    }

    private Page<Industry> getAllApproved(AuthorizationStatus authStatus, Pageable pageable) {
        return industryRepo.getAllApproved(authStatus, pageable);
    }

    private Page<Industry> getAllRejected(List<AuthorizationStatus> authStatusList, Pageable pageable) {
        return industryRepo.getAllRejected(authStatusList, pageable);
    }

    public Industry previewUpdate(Long id) throws CMMSException {
        Industry fromJson = industryRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        if (fromJson == null) {
            throw new CMMSException(Errors.NO_UPDATE_REQUEST.getValue(), "404", "404");
        }
        String jsonData = fromJson.getJsonData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Industry jsonUser = mapper.readValue(jsonData, Industry.class);
            return jsonUser;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error track ------ ",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(), "500", "500");
        }
    }
}
