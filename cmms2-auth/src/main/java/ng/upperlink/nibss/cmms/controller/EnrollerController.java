package ng.upperlink.nibss.cmms.controller;

import com.fasterxml.jackson.annotation.JsonView;
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

/**
 * Created by stanlee on 08/04/2018.
 */
@RestController
@RequestMapping("/agent/{agent-id}/enroller")
public class EnrollerController {

    /*private static Logger LOG = LoggerFactory.getLogger(AgentController.class);

    private EnrollerService enrollerService;
    private UserService userService;
    private UserType userType = UserType.ENROLLER;
    @Value("${email_from}")
    private String fromEmail;

    @Value("${encryption.salt}")
    private String salt;

    @Autowired
    public void setEnrollerService(EnrollerService enrollerService) {
        this.enrollerService = enrollerService;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @JsonView(View.ThirdParty.class)
    @GetMapping
    public ResponseEntity getAgentEnrollersByThirdParty(@PathVariable("agent-id") Long agtId, @RequestParam int pageNumber, @RequestParam int pageSize){
        return ResponseEntity.ok(enrollerService.get(agtId, new PageRequest(pageNumber,pageSize)));
    }

    @ApiIgnore
    @GetMapping("/upl")
    public ResponseEntity getAgentEnrollers(@PathVariable("agent-id") Long agtId, @RequestParam int pageNumber, @RequestParam int pageSize){
        return ResponseEntity.ok(enrollerService.get(agtId, new PageRequest(pageNumber,pageSize)));
    }


    @JsonView(View.ThirdParty.class)
    @GetMapping("/{id}")
    public ResponseEntity getAgentEnrollerByThirdParty(@PathVariable("agent-id") Long agtId, @PathVariable Long id){
        return ResponseEntity.ok(enrollerService.get(id,agtId));
    }

    @ApiIgnore
    @GetMapping("/{id}/upl")
    public ResponseEntity getAgentEnroller(@PathVariable("agent-id") Long agtId, @PathVariable Long id){
        return ResponseEntity.ok(enrollerService.get(id,agtId));
    }

    @JsonView(View.ThirdParty.class)
    @PostMapping
    public ResponseEntity createEnrollerByThirdParty(@Valid @RequestBody EnrollerRequest request){
        return create(request);
    }

    @ApiIgnore
    @PostMapping("/upl")
    public ResponseEntity createEnroller(@Valid @RequestBody EnrollerRequest request){
        return create(request);
    }

    @JsonView(View.ThirdParty.class)
    @PutMapping
    public ResponseEntity updateEnrollerByThirdParty(@Valid @RequestBody EnrollerRequest request){
        return update(request);
    }

    @ApiIgnore
    @PutMapping("/upl")
    public ResponseEntity updateEnroller(@Valid @RequestBody EnrollerRequest request){
        return update(request);
    }

    @ApiIgnore
    @PutMapping("/toggle")
    public ResponseEntity toggleEnroller(@PathVariable("agent-id") Long agtId, @Valid @RequestBody Id id){
        return ResponseEntity.ok(enrollerService.toggle(id.getId(), agtId));
    }

    private ResponseEntity create(EnrollerRequest request){

        String errorResult = enrollerService.validate(request, false, null);
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        String generatedCode = request.getCode();
        if (request.isAutoGeneratedCode()) {
            generatedCode = enrollerService.generateCode();
            if (Constants.EMPTY.equals(generatedCode)) {
                return ResponseEntity.badRequest().body(new ErrorDetails("Unable to generate code after 4 trials. please use manual process"));
            }
        }else {

            if(request.getCode() == null || request.getCode().isEmpty()){
                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.INVALID_DATA_PROVIDED.getValue().replace("{}", "Code")));
            }

            if (enrollerService.getCountOfSameCode(generatedCode, null) > 0){
                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{}", "Code").replace("{name}", generatedCode)));
            }
        }

        //email address must be unique
        if (enrollerService.getCountOfSameEmailAddress(request.getEmailAddress(), null) > 0) {
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{}", "Email address").replace("{name}", request.getEmailAddress())));
        }

        //username must be unique
        if (enrollerService.getCountOfSameUsername(request.getUsername(), null) > 0) {
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{}", "Username").replace("{name}", request.getUsername())));
        }


        Enroller enroller = enrollerService.generate(new Enroller(), request, false, generatedCode, userType);
        Long originalId = enroller.getUser().getId();

        boolean isUpdate = true;
        String password = null;

        if (enroller.getUser().getId() == null){
            LOG.info("Id is null ----> therefore this is a new user");
            LOG.info("the old Id is {}", originalId);
            isUpdate = false;
            password = userService.generatePassword();
            enroller.getUser().setId(originalId);
            enroller.getUser().setPassword(EncyptionUtil.doSHA512Encryption(password, salt));
            enroller.getUser().setPasswordUpdatedAt(new Date());
        }


        enroller = enrollerService.save(enroller);
        userService.sendMail(enroller.getUser(), null, password, isUpdate);

        return ResponseEntity.ok(enroller);
    }

    private ResponseEntity update(EnrollerRequest request){

        String errorResult = enrollerService.validate(request, true, request.getId());
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        Enroller enroller = enrollerService.get(request.getId(), request.getAgentId());
        if (enroller == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Unknown enroller id provided."));
        }

        if (enrollerService.getCountOfSameCode(request.getCode(), request.getId()) > 0){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{}", "Code").replace("{name}", request.getCode())));
        }

        //email address must be unique
        if (enrollerService.getCountOfSameEmailAddress(request.getEmailAddress(), enroller.getUser().getId()) > 0) {
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{}", "Email address").replace("{name}", request.getEmailAddress())));
        }

        //username must be unique
        if (enrollerService.getCountOfSameUsername(request.getUsername(), enroller.getUser().getId()) > 0) {
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.DATA_NAME_ALREADY_EXIST.getValue().replace("{}", "Username").replace("{name}", request.getUsername())));
        }

        enroller = enrollerService.generate(enroller, request, true, request.getCode(), userType);
        enroller = enrollerService.save(enroller);

        return ResponseEntity.ok(enroller);
    }

*/

}
