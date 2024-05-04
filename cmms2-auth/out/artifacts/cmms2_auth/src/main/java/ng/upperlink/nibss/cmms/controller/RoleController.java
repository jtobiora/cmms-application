package ng.upperlink.nibss.cmms.controller;

import com.fasterxml.jackson.annotation.JsonView;
import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.NewPrivilegeRequest;
import ng.upperlink.nibss.cmms.dto.Role;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.Privilege;
import ng.upperlink.nibss.cmms.service.PrivilegeService;
import ng.upperlink.nibss.cmms.service.RoleService;
import ng.upperlink.nibss.cmms.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by stanlee on 07/04/2018.
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    public static Logger LOG = LoggerFactory.getLogger(RoleController.class);

    private RoleService roleService;
    private PrivilegeService privilegeService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setPrivilegeService(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @ApiIgnore
    @GetMapping("/upl")
    public ResponseEntity getUserRoles(@RequestParam int pageNumber, @RequestParam int pageSize){
        return ResponseEntity.ok(roleService.getAll(new PageRequest(pageNumber,pageSize)));
    }

    @JsonView(View.ThirdParty.class)
    @GetMapping
    public ResponseEntity getUserRolesByThirdParty(){
        return ResponseEntity.ok(roleService.getActivated(UserType.SUBSCRIBER));
    }

//    @ApiIgnore
    @GetMapping("/activated/{userType}")
    public ResponseEntity getActivatedUserRoles(@PathVariable("userType") UserType userType){
        return ResponseEntity.ok(roleService.getActivated(userType));
    }

//    @ApiIgnore
    @PostMapping
    public ResponseEntity createUserRole(@Valid @RequestBody Role role){

        String errorResult = roleService.validate(role, false, null);
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        ng.upperlink.nibss.cmms.model.Role newRole = new ng.upperlink.nibss.cmms.model.Role();
        newRole.setDescription(role.getDescription());
        newRole.setName(role.getName());
        newRole.setUserType(role.getUserType());
        newRole = roleService.save(newRole);

        return ResponseEntity.ok(newRole);
    }

//    @ApiIgnore
    @PutMapping
    public ResponseEntity updateUserRole(@Valid @RequestBody Role role){

        String errorResult = roleService.validate(role, true, role.getId());
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        ng.upperlink.nibss.cmms.model.Role newRole = roleService.get(role.getId());

        if (newRole == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Unknown Role with the id '"+role.getId()+"'"));
        }

        newRole.setDescription(role.getDescription());
        newRole.setName(role.getName());
        newRole.setUserType(role.getUserType());
        newRole = roleService.save(newRole);

        return ResponseEntity.ok(newRole);
    }

    //@ApiIgnore
    @PutMapping("/toggle")
    public ResponseEntity toggleUserRole(@Valid @RequestBody Id id){
        return ResponseEntity.ok(roleService.toggle(id.getId()));
    }

//    @ApiIgnore
    @PutMapping("/privilege")
    public ResponseEntity assignTaskToUserRole(@Valid @RequestBody NewPrivilegeRequest request){

        ng.upperlink.nibss.cmms.model.Role role = roleService.get(request.getId());
        if (role == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Unknown role id provided"));
        }

        Set<Privilege> privileges = privilegeService.get(request.getPrivilegeIds());
        role.getPrivileges().clear();
        role.setPrivileges(privileges);

        role = roleService.save(role);
        return ResponseEntity.ok(role);

    }

//    @ApiIgnore
    @GetMapping("/{id}/privilege")
    public ResponseEntity privilegePerRole(@PathVariable Long id){

        if (id == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Invalid data provided"));
        }

        ng.upperlink.nibss.cmms.model.Role role = roleService.get(id);
        Set<Privilege> privilegeSet;
        if (role == null){
            privilegeSet = new HashSet<>();
        }else {
            privilegeSet = role.getPrivileges();
        }

        return ResponseEntity.ok(privilegeSet);
    }

}
