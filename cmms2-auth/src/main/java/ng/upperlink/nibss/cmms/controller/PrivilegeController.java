package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.auth.PrivilegeRequest;
import ng.upperlink.nibss.cmms.enums.Module;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.auth.Privilege;
import ng.upperlink.nibss.cmms.service.auth.PrivilegeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * Created by stanlee on 07/04/2018.
 */
//@ApiIgnore
@RestController
@RequestMapping("/privilege")
public class PrivilegeController {

    private static Logger LOG = LoggerFactory.getLogger(PrivilegeController.class);

    private PrivilegeService privilegeService;

    @Autowired
    public void setPrivilegeService(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @GetMapping
    public ResponseEntity getPrivileges(){
        return ResponseEntity.ok(privilegeService.get());
    }

    @GetMapping("/activated")
    public ResponseEntity getActivatedPrivileges(){
        return ResponseEntity.ok(privilegeService.getAllActivated());
    }

    @PostMapping
    public ResponseEntity createPrivilege(@Valid @RequestBody PrivilegeRequest taskRequest){

        String errorResult = privilegeService.validate(taskRequest, false, null);
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        Privilege privilege = new Privilege();
        privilege.setDescription(taskRequest.getDescription());
        privilege.setModule(Module.valueOf(taskRequest.getModule()));
        privilege.setName(taskRequest.getName());
        privilege.setUrl(taskRequest.getUrl());
        privilege = privilegeService.save(privilege);

        return ResponseEntity.ok(privilege);
    }

    @PutMapping
    public ResponseEntity updatePrivilege(@Valid @RequestBody PrivilegeRequest taskRequest){

        String errorResult = privilegeService.validate(taskRequest, true, taskRequest.getId());
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        Privilege privilege = privilegeService.get(taskRequest.getId());

        if (privilege == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Unknown Privilege with the id '"+taskRequest.getId()+"'"));
        }

        privilege.setDescription(taskRequest.getDescription());
        privilege.setModule(Module.valueOf(taskRequest.getModule()));
        privilege.setName(taskRequest.getName());
        privilege.setUrl(taskRequest.getUrl());
        privilege = privilegeService.save(privilege);

        return ResponseEntity.ok(privilege);
    }

    @PutMapping("/toggle")
    public ResponseEntity togglePrivilege(@Valid @RequestBody Id id){
        return ResponseEntity.ok(privilegeService.toggle(id.getId()));
    }
}
