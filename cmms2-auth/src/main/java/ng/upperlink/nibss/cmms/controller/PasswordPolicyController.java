/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.controller;

import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.auth.PasswordPolicyRequest;
import ng.upperlink.nibss.cmms.dto.auth.PasswordPolicyResponse;
import ng.upperlink.nibss.cmms.dto.auth.PasswordUpdateRequest;
import ng.upperlink.nibss.cmms.model.auth.PasswordPolicySetting;
import ng.upperlink.nibss.cmms.service.auth.PasswordValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @author cmegafu
 */
@RestController
@RequestMapping("/password-policy")
@Slf4j
public class PasswordPolicyController {
    
    @Autowired
    private PasswordValidationService passwordPolicyService;
    
    // get password policy settings
    @GetMapping("/settings")
    public ResponseEntity getPasswordPolicySetting() {
        return ResponseEntity.ok(passwordPolicyService.policySettings());
    }
    
    @PutMapping("/update")
    public ResponseEntity updatePasswordPolicy(@RequestBody PasswordUpdateRequest request) {

        PasswordPolicySetting setting = passwordPolicyService.policySettings();
        setting.setMaxAllowedPassword(request.getMaxAllowedPassword());
        setting.setMaximumLength(request.getMaximumLength());
        setting.setMinimumLength(request.getMinimumLength());
        setting.setUpdatePeriod(request.getUpdatePeriod());
        if (null != passwordPolicyService.updatePasswordPolicySetting(setting)) {
            return ResponseEntity.ok().body(new PasswordPolicyResponse("Settings successfully updated", "00"));
        } else {
            return ResponseEntity.badRequest().body(new PasswordPolicyResponse("Unable to update password policy settings, please try again", "22"));
        }
    }
    
    @RequestMapping(value="/ping", method = GET)
    public String ping() {
        return "Password policy is working";
    }
    
    @RequestMapping(value="/validate", method=POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PasswordPolicyResponse validatePassword(@RequestBody PasswordPolicyRequest request, HttpServletResponse httpServletResponse) {
        PasswordPolicyResponse response = passwordPolicyService.isValid(request.getUsername(), request.getPassword());
        if (null != response && null != response.getResponseCode() && response.getResponseCode().trim().equals("00")) 
            httpServletResponse.setStatus(HttpStatus.OK.value());
        else 
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        return response;
    }
}
