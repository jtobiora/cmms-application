package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.mandates.MandateResponse;
import ng.upperlink.nibss.cmms.dto.mandates.RejectionRequests;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.MandateFrequency;
import ng.upperlink.nibss.cmms.enums.MandateStatusType;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.*;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateService;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateStatusService;
import ng.upperlink.nibss.cmms.service.mandateImpl.RejectionReasonsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/operations")
public class BaseController {
    private Logger logger = LoggerFactory.getLogger(BaseController.class);
    private MandateService mandateService;
    private MandateStatusService mandateStatusService;
    private UserService userService;
    private RejectionReasonsService rejectionReasonsService;

    @Autowired
    public void setRejectionReasonsService(RejectionReasonsService rejectionReasonsService){
        this.rejectionReasonsService = rejectionReasonsService;
    }

    @Autowired
    public void setMandateStatusService(MandateStatusService mandateStatusService){
        this.mandateStatusService = mandateStatusService;
    }

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void setMandateService(MandateService mandateService){
        this.mandateService = mandateService;
    }


    @RequestMapping(value = "/mandateStatuses/fetch", method = RequestMethod.GET)
    public ResponseEntity<Object> getMandateStatuses(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){

        User userOperator = userService.get(userDetail.getUserId());
        if (userOperator == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        if (userOperator instanceof BankUser){
            return new ResponseEntity<Object>(mandateStatusService.getMandateStatuses("bankUsers"),HttpStatus.OK);
        }

        //check if equal to billerUser too

        return null;
    }

    @RequestMapping(value = "/rejectionReasons", method = RequestMethod.GET)
    public ResponseEntity<Object> getMandateRejectedReasons(){
        return new ResponseEntity<Object>(rejectionReasonsService.getAll(),HttpStatus.OK);
    }

    @RequestMapping(value = "/rejectionReasons/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getMandateRejectedReasonsById(@PathVariable("id")Long id){
        return new ResponseEntity<Object>(rejectionReasonsService.getOne(id),HttpStatus.OK);
    }



}
