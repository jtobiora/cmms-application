package ng.upperlink.nibss.cmms.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.auth.NewPrivilegeRequest;
import ng.upperlink.nibss.cmms.dto.auth.Role;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.RoleType;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.auth.Privilege;
import ng.upperlink.nibss.cmms.service.auth.PrivilegeService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by stanlee on 07/04/2018.
 */
@RestController
@RequestMapping("/role")
@Slf4j
public class RoleController {

    //public static Logger log = LoggerFactory.getLogger(RoleController.class);

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
    public ResponseEntity getUserRoles(@RequestParam int pageNumber, @RequestParam int pageSize) {
        return ResponseEntity.ok(roleService.getAll(new PageRequest(pageNumber, pageSize)));
    }

    @JsonView(View.ThirdParty.class)
    @GetMapping("{userType}")
    public ResponseEntity getUserRolesByUserType(@PathVariable("userType") UserType userType) {
        try{
            return ResponseEntity.ok(roleService.getActivated(userType));
        }catch(Exception e){
            log.error("Could not load users by {} userType",userType,e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    @ApiIgnore
    @GetMapping("/activated/{userType}")
    public ResponseEntity getActivatedUserRoles(@PathVariable("userType") UserType userType) {
        return ResponseEntity.ok(roleService.getActivated(userType));
    }

    @GetMapping("/activated/roleType/{userType}")
    public ResponseEntity getActivatedUserRolesByType(@PathVariable("userType") UserType userType,
                                                      @ApiIgnore @RequestAttribute(Constants.USER_DETAIL)UserDetail userDetail)
    {
        try {
            return ResponseEntity.ok(roleService.getActivatedByRoleType(userType,userDetail));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to retrieve", Arrays.asList(e.getMessage()),e.getCode());
        }
    }

    //    @ApiIgnore
    @PostMapping
    public ResponseEntity createUserRole(@Valid @RequestBody Role role) {
        String errorResult = roleService.validate(role, false, null);
        if (errorResult != null) {
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        ng.upperlink.nibss.cmms.model.auth.Role newRole = new ng.upperlink.nibss.cmms.model.auth.Role();
        newRole.setDescription(role.getDescription());
        newRole.setName(role.getName());
        newRole.setRoleType(role.getRoleType());
        newRole.setUserType(role.getUserType());
        newRole.setUserAuthorisationType(role.getUserAuthorisationType());
        newRole = roleService.save(newRole);

        return ResponseEntity.ok(newRole);
    }

    //    @ApiIgnore
    @PutMapping
    public ResponseEntity updateUserRole(@Valid @RequestBody Role role) {

        String errorResult = roleService.validate(role, true, role.getId());
        if (errorResult != null) {
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        ng.upperlink.nibss.cmms.model.auth.Role newRole = roleService.get(role.getId());

        if (newRole == null) {
            return ResponseEntity.badRequest().body(new ErrorDetails("Unknown Role with the id '" + role.getId() + "'"));
        }

        newRole.setDescription(role.getDescription());
        newRole.setName(role.getName());
        newRole.setUserType(role.getUserType());
        newRole.setRoleType(role.getRoleType());
        newRole.setRoleType(role.getRoleType());
        newRole = roleService.save(newRole);

        return ResponseEntity.ok(newRole);
    }

    //@ApiIgnore
    @PutMapping("/toggle")
    public ResponseEntity toggleUserRole(@Valid @RequestBody Id id) {
        return ResponseEntity.ok(roleService.toggle(id.getId()));
    }

    //    @ApiIgnore
    @PutMapping("/privilege")
    public ResponseEntity assignTaskToUserRole(@Valid @RequestBody NewPrivilegeRequest request) {

        ng.upperlink.nibss.cmms.model.auth.Role role = roleService.get(request.getId());
        if (role == null) {
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
    public ResponseEntity privilegePerRole(@PathVariable Long id) {

        if (id == null) {
            return ResponseEntity.badRequest().body(new ErrorDetails("Invalid data provided"));
        }

        ng.upperlink.nibss.cmms.model.auth.Role role = roleService.get(id);
        Set<Privilege> privilegeSet;
        if (role == null) {
            privilegeSet = new HashSet<>();
        } else {
            privilegeSet = role.getPrivileges();
        }

        return ResponseEntity.ok(privilegeSet);
    }

}
