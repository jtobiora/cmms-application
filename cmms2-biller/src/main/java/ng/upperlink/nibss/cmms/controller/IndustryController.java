package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.IndustryRequest;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.enums.makerchecker.ViewAction;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.biller.Industry;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.biller.IndustryService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Optional;

/**
 * REST controller for managing Industry.
 */
@RestController
@RequestMapping("/industry")
public class IndustryController {

    private final Logger log = LoggerFactory.getLogger(IndustryController.class);

    IndustryService industryService;
    UserService userService;

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
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createIndustry(@Valid @RequestBody IndustryRequest industryRequest,
                                            @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){


        Industry industry = null;
        try {
            industry = industryService.setUp(industryRequest, userDetail,false,null, InitiatorActions.CREATE);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ResponseEntity.status(Integer.valueOf(e.getCode())).body(e.getMessage());
        }catch(Exception e){
            log.error("Could not create industry",e);
            return new ResponseEntity<>(new ErrorDetails("Unable to perform create action."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(industry);
    }

//    private Industry setUp(IndustryRequest industryRequest, UserDetail userDetail) {
//        User operatorUser = userService.get(userDetail.getUserId());
//        if (operatorUser == null)
//        {
//            throw new CMMSException("Please login and try again","401","401");
//        }
//        Collection<Role> roles = operatorUser.getRoles();
//        if (roles == null)
//        {
//            throw new CMMSException("You have no role","401","401");
//        }
//
//        Role operatorRole = roles.stream().findAny().get();
//
//        if (!operatorRole.getName().equals(RoleName.NIBSS_SUPER_ADMIN_INITIATOR))
//        {
//            throw new CMMSException("Please login and try again","401","401");
//        }
//
//        //make sure that the bank code is not null and doesn't already exist
//        industryService.validate(industryRequest, false, null);
//        Industry industry = industryService.generate(new Industry(), industryRequest, operatorUser, false);
//        industry = industryService.save(industry);
//        return industry;
//    }

    @PutMapping
    public ResponseEntity<?> updateIndustry(@Valid @RequestBody IndustryRequest industryRequest,
                                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){

        Industry industry = null;
        try {
            industry = industryService.setUp(industryRequest, userDetail,true,null, InitiatorActions.UPDATE);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ResponseEntity.status(Integer.valueOf(e.getCode())).body(e.getMessage());
        }catch(Exception e){
            log.error("Industry could not be updated",e);
            return new ResponseEntity<>(new ErrorDetails("Updates could not be performed."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(industry);
    }
    @GetMapping()
    public ResponseEntity getAllActivated(@Valid @RequestParam Optional<Integer> pageNumber, @RequestParam Optional<Integer> pageSize){

       try {
           if(pageNumber.isPresent()&& pageSize.isPresent()){
               int pNum = pageNumber.get();
               int pSize = pageSize.get();
               return ResponseEntity.ok(industryService.getAllActivated(new PageRequest(pNum,pSize)));
           }else

               return ResponseEntity.ok(industryService.getAllActivated());
       }catch(Exception e){
           log.error("Could not complete request",e);
           return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/industry/{id}")
    public ResponseEntity<?> getIndustry(@PathVariable Long id) {
        log.debug("REST request to get Industry : {}", id);

        try {
            return ResponseEntity.ok(industryService.getOne(id));
        }catch(Exception e){
            log.error("Could not retrieve industry with id {} ",id,e);
            return new ResponseEntity<>(new ErrorDetails("Unable to load details."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/industry/{id}")
    public ResponseEntity<?> deleteIndustry(@PathVariable Long id) {
        log.debug("REST request to delete Industry : {}", id);

        return ResponseEntity.ok(industryService.delete(id));
    }

    @GetMapping("/viewAction")
    public ResponseEntity getAllPendingUsere(@RequestParam ViewAction viewAction, @RequestParam int pageNumber, @RequestParam int pageSize){
        try{
            return ResponseEntity.ok(industryService.selectView(viewAction,new PageRequest(pageNumber,pageSize)));
        }catch (Exception e){
            log.error("Could not complete request",e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/previewUpdate")
    public ResponseEntity previewUpdate ( @RequestParam Long id,
                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail)
    {
        try {
            return ResponseEntity.status(201).body(industryService.previewUpdate(id));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ResponseEntity.status(Integer.valueOf(e.getCode())).body(e.getMessage());
        }catch(Exception e){
            log.error("Could not complete request",e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/authorization")
    public ResponseEntity authorizationAction(@Valid @RequestBody AuthorizationRequest request, @RequestParam AuthorizationAction action,
                                              @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        Industry industry = null;
        try {
            industry = (Industry) otherAuthorizationService.performAuthorization(request, action, userDetail, EntityType.INDUSTRY);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ResponseEntity.status(Integer.valueOf(e.getCode())).body(e.getMessage());
        }catch(Exception e){
            log.error("Authorization failed.",e);
            return new ResponseEntity<>(new ErrorDetails("Authorization failed."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(200).body(industry);
    }


}
