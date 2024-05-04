package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.NibssRequest;
import ng.upperlink.nibss.cmms.dto.Reason;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.search.PageSearch;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.MakerCheckerType;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.NibssUser;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.service.NibssService;
import ng.upperlink.nibss.cmms.service.UserService;
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
//@RestController
@RequestMapping("/user/nibss")
public class NibssController {

    private static Logger LOG = LoggerFactory.getLogger(NibssController.class);

    private NibssService nibssService;
    private UserService userService;
    private SmtpMailSender smtpMailSender;

    @Value("${email_from}")
    private String fromEmail;

    @Value("${encryption.salt}")
    private String salt;

    private UserType userType = UserType.NIBSS;

    @Autowired
    public void setNibssService(NibssService nibssService) {
        this.nibssService = nibssService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }

    @GetMapping
    public ResponseEntity getNibssUsers(@RequestParam int pageNumber, @RequestParam int pageSize){
        return ResponseEntity.ok(nibssService.get(new PageRequest(pageNumber,pageSize)));
    }

    @GetMapping("/{id}")
    public ResponseEntity getNibssUser(@PathVariable Long id){
        return ResponseEntity.ok(nibssService.get(id));
    }

    @PostMapping
    public ResponseEntity createNibssUser(@Valid @RequestBody NibssRequest request,
                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){

        //Ensure that it is only an OPERATOR that can do this action;
        if (MakerCheckerType.OPERATOR != MakerCheckerType.valueOf(userDetail.getUserAuthorizationType())){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        String errorResult = nibssService.validate(request, false, null);
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        User OperatorUser = userService.get(userDetail.getUserId());
        if (OperatorUser == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        //email address must be unique
        if (nibssService.getCountOfSameEmailAddress(request.getEmailAddress(), null) > 0) {
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{}", "Email address").replace("{name}", request.getEmailAddress())));
        }

        NibssUser nibss = nibssService.generate(new NibssUser(), request, new User(), OperatorUser, false, userType);

        nibss = nibssService.generateForApproval(nibss);

        //save the user object first before the actual nibss object
//        User user = userService.save(nibss.getUser());
//        nibss.setUser(user);

        nibss = nibssService.save(nibss);

        //send a mail to all the authorizers for awareness
        sendApprovalEmail(nibss,false);

        return ResponseEntity.ok(nibss);
    }

    @PutMapping
    public ResponseEntity updateNibssUser(@Valid @RequestBody NibssRequest request,
                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){

        //Ensure that it is only an OPERATOR that can do this action;
        if (MakerCheckerType.OPERATOR != MakerCheckerType.valueOf(userDetail.getUserAuthorizationType())){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        String errorResult = nibssService.validate(request, true, request.getId());
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        NibssUser nibss = nibssService.get(request.getId());
        if (nibss == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Unknown id provided."));
        }

        User OperatorUser = userService.get(userDetail.getUserId());
        if (OperatorUser == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        //email address must be unique
        if (nibssService.getCountOfSameEmailAddress(request.getEmailAddress(), nibss.getId()) > 0) {
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{}", "Email address").replace("{name}", request.getEmailAddress())));
        }

        User user = new User();
        user.setId(nibss.getId());
        user.setEmailAddress(nibss.getEmailAddress());
        user.setPassword(nibss.getPassword());
        user.setChange_password(nibss.isChange_password());

        NibssUser unapprovedData = nibssService.generate(new NibssUser(), request, user, OperatorUser, true, userType);
        nibss = nibssService.generateForApproval(nibss,unapprovedData);
        nibss = nibssService.save(nibss);

        //send a mail to all the authorizers for awareness
        sendApprovalEmail(nibss,true);

        return ResponseEntity.ok(nibss);
    }

    @PutMapping("/toggle")
    public ResponseEntity toggleNibssUser(@Valid @RequestBody Id id,
                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){

        //Ensure that it is only an AUTHORIZER that can do this action;
        if (MakerCheckerType.AUTHORIZER != MakerCheckerType.valueOf(userDetail.getUserAuthorizationType())){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        User user = userService.get(userDetail.getUserId());
        if (user == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        return ResponseEntity.ok(nibssService.toggle(id.getId(), user));
    }

    @GetMapping("/approval/{approvalStatus}")
    public ResponseEntity institutionByApprovalStatus(@PathVariable(value = "approvalStatus") String approvalStatus, PageSearch pageSearch){
        return ResponseEntity.ok(nibssService.getJsonData(approvalStatus, pageSearch));
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

        NibssUser nibss = nibssService.getUnapprovedById(id.getId());
        Long originalId = nibss.getId();
        if (nibss == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.ALREADY_APPROVED_OR_DISAPPROVED.getValue()));
        }

        nibss = nibssService.generate(nibss, nibss.getMakerChecker().getUnapprovedData(), user, Boolean.TRUE, null);

        //the user object first then the NibssUser object -- remember to send an email containing the Username and password

        boolean isUpdate = true;
        String password = null;

        LOG.info("nibss user details is {}", nibss);
        LOG.info("the emailAddress is {}", nibss.getEmailAddress());
        LOG.info("the new Id is {}", nibss.getId());

        if (nibss.getId() == null){
            LOG.info("Id is null ----> therefore this is a new user");
            LOG.info("the old Id is {}", originalId);
            isUpdate = false;
            password = userService.generatePassword();
            nibss.setId(originalId);
            nibss.setPassword(EncyptionUtil.doSHA512Encryption(password, salt));
            nibss.setPasswordUpdatedAt(new Date());
        }

        try {
            //save the user object first before the actual nibss object
//            user = userService.save(nibss.getUser());
//            nibss.setUser(user);
        } catch (Exception e) {
            LOG.error("Unable save user details --> {}", e);
            return ResponseEntity.badRequest().body(new ErrorDetails("Unable to approve this user, Please try again"));
        }

        //send mail to the user
        userService.sendMail(user, null, password, isUpdate);

        //save
        nibss = nibssService.save(nibss);

        //send a mail to the createdBy/updatedBy
        sendAwarenessEmail(nibss, true, true);

        return ResponseEntity.ok(nibss);

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

        NibssUser nibss = nibssService.getUnapprovedById(reason.getId());
        if (nibss == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.ALREADY_APPROVED_OR_DISAPPROVED.getValue()));
        }

        nibss = nibssService.generate(nibss, nibss.getMakerChecker().getUnapprovedData(), user, Boolean.FALSE, reason.getReason());
        nibss = nibssService.save(nibss);

        //send a mail to the createdBy/updatedBy
        sendAwarenessEmail(nibss, true, false);

        return ResponseEntity.ok(nibss);

    }

    private void sendApprovalEmail(NibssUser nibss, boolean isUpdate){

        List<String> emailAddress = nibssService.getAllActiveAuthorizerEmailAddress();

        System.out.println(emailAddress);

        if(!emailAddress.isEmpty()){

            String message = "A User "+nibss.getName().getLastName()+" "+nibss.getName().getFirstName()+" was created. Please log on to the portal to approve or disapprove the user";
            if (isUpdate){
                message = "The User "+nibss.getName().getLastName()+" "+nibss.getName().getFirstName()+" was updated. Please log on to the portal to approve or disapprove the update";
            }

            //Send the mail

            smtpMailSender.sendMail(fromEmail, emailAddress.toArray(new String[emailAddress.size()]), "User Approval awareness",
                    "User Approval Awareness", message, generateDetails(nibss, isUpdate));
        }
    }

    private void sendAwarenessEmail(NibssUser nibss, boolean isUpdated, boolean isApproved){


        if (nibss.getCreatedBy() == null){
            LOG.error("NIBSS User 'created by' is NULL");
            return;
        }

        if (isUpdated && nibss.getUpdatedBy() == null){
            LOG.error("NIBSS User 'updated by' is NULL");
            return;
        }

        String emailAddress = nibss.getCreatedBy().getEmailAddress();
        if (isUpdated){
            emailAddress = nibss.getUpdatedBy().getEmailAddress();
        }

        String[] email = {emailAddress};

        if(email.length > 0){

            String message = "The User "+nibss.getName().getLastName()+" "+nibss.getName().getFirstName()+" have been "+(isApproved ? "approved" : "disapproved");
            if (isUpdated){
                message = "The changes on the User "+nibss.getName().getLastName()+" "+nibss.getName().getFirstName()+" have been "+(isApproved ? "approved" : "disapproved");
            }

            //Send the mail
            smtpMailSender.sendMail(fromEmail, email, "User Approval awareness",
                    "User Approval Awareness", message, generateDetails(nibss, true));
        }

    }


    private String generateDetails(NibssUser nibss, boolean isUpdate){

        String details = "";

        details += "<strong>Name :</strong> " +nibss.getName().getLastName()+" "+nibss.getName().getFirstName()+ "<br/>";
        details += "<strong>Code :</strong> " + nibss.getStaffNumber() + "<br/>";

        return details;
    }
}
