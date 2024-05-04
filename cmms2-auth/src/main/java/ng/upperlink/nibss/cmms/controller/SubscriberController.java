package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.dto.*;
import ng.upperlink.nibss.cmms.dto.SubscriberRequest;
import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.dto.search.PageSearch;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.Subscriber;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.service.*;
import ng.upperlink.nibss.cmms.service.auth.ResetService;
import ng.upperlink.nibss.cmms.service.nibss.NibssUserService;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * Created by stanlee on 08/04/2018.
 */
//@ApiIgnore
@RestController
@RequestMapping("/user/agt-mgr")
public class SubscriberController {

    private static Logger LOG = LoggerFactory.getLogger(SubscriberController.class);

    private SubscriberService subscriberService;
    private NibssUserService nibssUserService;
    private ResetService resetService;
    private UserService userService;
    private UserType userType = UserType.SUBSCRIBER;
    private SmtpMailSender smtpMailSender;

    @Value("${email_from}")
    private String fromEmail;

    @Value("${encryption.salt}")
    private String salt;


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }
    @Autowired
    public void setSubscriberService(SubscriberService subscriberService) {this.subscriberService = subscriberService;}
    @Autowired
    public void setResetService(ResetService resetService) {this.resetService = resetService;}
    @Autowired
    public void setResetService(NibssUserService nibssUserService) {this.nibssUserService = nibssUserService;}

    @GetMapping
    public ResponseEntity getAllSubscriber(@RequestParam int pageNumber, @RequestParam int pageSize){
        return ResponseEntity.ok(subscriberService.get(new PageRequest(pageNumber,pageSize)));
    }

    @GetMapping("/{id}")
    public ResponseEntity getSubscriber(@PathVariable Long id){
        return ResponseEntity.ok(subscriberService.get(id));
    }

    @PostMapping
    public ResponseEntity createSubscriberUser(@Valid @RequestBody SubscriberRequest request,
                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        //Ensure that it is only an OPERATOR that can do this action;
        if (MakerCheckerType.OPERATOR != MakerCheckerType.valueOf(userDetail.getUserAuthorizationType())){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        try {
            subscriberService.validate(request, false, null);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ResponseEntity.status(Integer.valueOf(e.getCode())).body(e.getMessage());
        }

        User OperatorUser = userService.get(userDetail.getUserId());
        if (OperatorUser == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        User existingUser = userService.getByEmail(request.getEmailAddress());
        if (existingUser != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{name}"," email address "+request.getEmailAddress())));
        }
        existingUser = userService.getByEmail(request.getEmailAddress());
        if (existingUser != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{name}"," username "+request.getEmailAddress())));
        }

        String code = request.getCode();
        if (code!=null && !code.trim().isEmpty()) {
            if(subscriberService.countOfSameCode(request.getCode()) > 0){
                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{name}", "  code "+ request.getCode())));
            }
        }else {
            code = subscriberService.generateCode();
            if (Constants.EMPTY.equals(code)){
                return ResponseEntity.badRequest().body(new ErrorDetails("Unable to generate code after 4 trials. please use manual process"));
            }
            request.setCode(code);
            request.setAutoGeneratedCode(true);
        }

        Subscriber subscriber = subscriberService.generate(new Subscriber(), request, new User(), OperatorUser, false, userType);
        subscriber = subscriberService.generateForApproval(subscriber);

        //save the user object first before the actual nibss object
        try {

            User user = userService.save(subscriber.getUser());
            subscriber.setUser(user);
            subscriber = subscriberService.save(subscriber);
            //send a mail to all the authorizers for awareness

//            sendApprovalEmail(subscriber, false);
        }catch (Exception e){
            e.printStackTrace();
            LOG.error("Unable to create this agent manager user  --> {}", e);
            return ResponseEntity.badRequest().body(new ErrorDetails("Unable to create this agent manager user , Please try again "));
        }
        return ResponseEntity.ok(subscriber);
    }


    @PutMapping
    public ResponseEntity updateSubscriberUser(@Valid @RequestBody SubscriberRequest request,
                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){

        //Ensure that it is only an OPERATOR that can do this action;
        if (MakerCheckerType.OPERATOR != MakerCheckerType.valueOf(userDetail.getUserAuthorizationType())){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        try {
            subscriberService.validate(request, true, request.getId());
        } catch (CMMSException e) {
            e.printStackTrace();
            return ResponseEntity.status(Integer.valueOf(e.getCode())).body(e.getMessage());
        }

        Subscriber subscriber = subscriberService.get(request.getId());
        if (subscriber == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Unknown id provided."));
        }

        User OperatorUser = userService.get(userDetail.getUserId());
        if (OperatorUser == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        User user = new User();
        user.setId(subscriber.getUser().getId());
        user.setEmailAddress(subscriber.getUser().getEmailAddress());
        user.setPassword(subscriber.getUser().getPassword());
        user.setChange_password(subscriber.getUser().isChange_password());

        Subscriber unapprovedData = subscriberService.generate(new Subscriber(), request, user, OperatorUser, true, userType);

        subscriber = subscriberService.generateForApproval(subscriber,unapprovedData);
        subscriber = subscriberService.save(subscriber);

        //send a mail to all the authorizers for awareness
//        sendApprovalEmail(subscriber,true);

        return ResponseEntity.ok(subscriber);
    }

    @PutMapping("/toggle")
    public ResponseEntity toggleSubscriberUser(@Valid @RequestBody Id id,
                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        //Ensure that it is only an AUTHORIZER that can do this action;
        if (MakerCheckerType.AUTHORIZER != MakerCheckerType.valueOf(userDetail.getUserAuthorizationType())){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        User user = userService.get(userDetail.getUserId());
        if (user == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        return ResponseEntity.ok(subscriberService.toggle(id.getId(), user));
    }

    @GetMapping("/approval/{approvalStatus}")
    public ResponseEntity institutionByApprovalStatus(@PathVariable(value = "approvalStatus") String approvalStatus, PageSearch pageSearch){
        return ResponseEntity.ok(subscriberService.getJsonData(approvalStatus, pageSearch));
    }
    @PutMapping("/password/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody Id agentRequest,
                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){

        //Ensure that it is only an AUTHORIZER that can do this action;
        if (MakerCheckerType.AUTHORIZER != MakerCheckerType.valueOf(userDetail.getUserAuthorizationType())){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        Subscriber subscriber = subscriberService.get(agentRequest.getId());
        if (subscriber == null){
            ResponseEntity.badRequest().body(new ErrorDetails(Errors.INVALID_DATA_PROVIDED.getValue().replace("{}","agent manager id")));
        }

        User user = userService.get(userDetail.getUserId());
        if (user == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        String password = userService.generatePassword();
        subscriber.getUser().setPassword(EncyptionUtil.doSHA512Encryption(password, salt));
        subscriber.getUser().setPasswordUpdatedAt(new Date());
        subscriber.getUser().setChange_password(true);
        subscriber.setUpdatedBy(user);

        try {
            //save the user object
            userService.save(subscriber.getUser());
        } catch (Exception e) {
            LOG.error("Unable save user details --> {}", e);
            return ResponseEntity.badRequest().body(new ErrorDetails("Unable to reset this user password, Please try again"));
        }

        //update agent manager
        subscriberService.save(subscriber);

        //send mail to the user
        userService.sendMail(subscriber.getUser(), subscriber.getCode(), password, false);

        return ResponseEntity.ok(new SuccessResponse("Agent Manager password reset is successful. An email containing the password have been sent to the agent manager email address"));
    }



    @PutMapping("/approval/approve")
    public ResponseEntity approve(@Valid @RequestBody Id id,
                                  @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){

        //Ensure that it is only an AUTHORIZER that can do this action;
        if (MakerCheckerType.AUTHORIZER != MakerCheckerType.valueOf(userDetail.getUserAuthorizationType())){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        User user = userService.get(userDetail.getUserId());
        if (user == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        Subscriber subscriber = subscriberService.getUnapprovedById(id.getId());
        if (subscriber == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.ALREADY_APPROVED_OR_DISAPPROVED.getValue()));
        }
        Long originalId = subscriber.getUser().getId();

        subscriber = subscriberService.generate(subscriber, subscriber.getMakerChecker().getUnapprovedData(), user, Boolean.TRUE, null);

        //the user object first then the Nibss object -- remember to send an email containing the Username and password

        boolean isUpdate = true;
        String password = null;

        LOG.info("nibss user details is {}", subscriber);
        LOG.info("the username is {}", subscriber.getUser().getEmailAddress());
        LOG.info("the new Id is {}", subscriber.getUser().getId());

        if (subscriber.getUser().getId() == null){
            LOG.info("Id is null ----> therefore this is a new user");
            LOG.info("the old Id is {}", originalId);
            isUpdate = false;
            password = userService.generatePassword();
            subscriber.getUser().setId(originalId);
            subscriber.getUser().setPassword(EncyptionUtil.doSHA512Encryption(password, salt));
            subscriber.getUser().setPasswordUpdatedAt(new Date());
        }

        try {
            //save the user object first before the actual nibss object
            user = userService.save(subscriber.getUser());
            subscriber.setUser(user);
        } catch (Exception e) {
            LOG.error("Unable save user details --> {}", e);
            return ResponseEntity.badRequest().body(new ErrorDetails("Unable to approve this user, Please try again"));
        }

        //send mail to the user
        userService.sendMail(user, null, password, isUpdate);

        //save
        subscriber = subscriberService.save(subscriber);

        //send a mail to the createdBy/updatedBy
        sendAwarenessEmail(subscriber, true, true);

        return ResponseEntity.ok(subscriber);

    }

    @PutMapping("/approval/disapprove")
    public ResponseEntity disapprove(@Valid @RequestBody Reason reason,
                                     @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){

        //Ensure that it is only an AUTHORIZER that can do this action;
        if (MakerCheckerType.AUTHORIZER != MakerCheckerType.valueOf(userDetail.getUserAuthorizationType())){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        User user = userService.get(userDetail.getUserId());
        if (user == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        Subscriber subscriber = subscriberService.getUnapprovedById(reason.getId());
        if (subscriber == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.ALREADY_APPROVED_OR_DISAPPROVED.getValue()));
        }

        subscriber = subscriberService.generate(subscriber, subscriber.getMakerChecker().getUnapprovedData(), user, Boolean.FALSE, reason.getReason());
        subscriber = subscriberService.save(subscriber);

        //send a mail to the createdBy/updatedBy
        sendAwarenessEmail(subscriber, true, false);

        return ResponseEntity.ok(subscriber);

    }

//    private void sendApprovalEmail(Subscriber subscriber, boolean isUpdate){
//
//        List<String>  emailAddress = nibssUserService.getAllActiveAuthorizerEmailAddress();
//
//        if(!emailAddress.isEmpty()){
//
//            String message = "A User "+subscriber.getUser().getName().getLastName()+" "+subscriber.getUser().getName().getFirstName()+" was created. Please log on to the portal to approve or disapprove the user";
//            if (isUpdate){
//                message = "The User "+subscriber.getUser().getName().getLastName()+" "+subscriber.getUser().getName().getFirstName()+" was updated. Please log on to the portal to approve or disapprove the update";
//            }
//
//            //Send the mail
//            smtpMailSender.sendMail(fromEmail, emailAddress.toArray(new String[emailAddress.size()]), "User Approval awareness",
//                    "User Approval Awareness", message, generateDetails(subscriber, isUpdate));
//        }
//    }

    private void sendAwarenessEmail(Subscriber subscriber, boolean isUpdated, boolean isApproved){


        if (subscriber.getCreatedBy() == null){
            LOG.error("SubscriberInstitution 'created by' is NULL");
            return;
        }

        if (isUpdated && subscriber.getUpdatedBy() == null){
            LOG.error("SubscriberInstitution 'updated by' is NULL");
            return;
        }

        String emailAddress = subscriber.getCreatedBy().getEmailAddress();
        if (isUpdated){
            emailAddress = subscriber.getUpdatedBy().getEmailAddress();
        }

        String[] email = {emailAddress};

        if(email.length > 0){

            String message = "The User "+subscriber.getUser().getName().getLastName()+" "+subscriber.getUser().getName().getFirstName()+" have been "+(isApproved ? "approved" : "disapproved");
            if (isUpdated){
                message = "The changes on the User "+subscriber.getUser().getName().getLastName()+" "+subscriber.getUser().getName().getFirstName()+" have been "+(isApproved ? "approved" : "disapproved");
            }

            //Send the mail
            smtpMailSender.sendMail(fromEmail, email, "User Approval awareness",
                    "User Approval Awareness", message, generateDetails(subscriber, true));
        }

    }
    private String generateDetails(Subscriber subscriber, boolean isUpdate){

        String details = "";

        details += "<strong>Name :</strong> " +subscriber.getUser().getName().getLastName()+" "+subscriber.getUser().getName().getFirstName()+ "<br/>";
        details += "<strong>Code :</strong> " + subscriber.getCode() + "<br/>";

        return details;
    }

}
