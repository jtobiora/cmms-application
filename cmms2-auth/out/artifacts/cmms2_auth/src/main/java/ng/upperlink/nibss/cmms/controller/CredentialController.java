package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.enums.ServiceName;
import ng.upperlink.nibss.cmms.service.ResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by stanlee on 03/05/2018.
 */
@RestController
public class CredentialController {

    private ResetService resetService;

    @Autowired
    public void setResetService(ResetService resetService) {
        this.resetService = resetService;
    }

    @PostMapping(name = "reset-credential", value = "/Reset")
    public ResponseEntity reset(@RequestHeader String OrganisationCode){
        return resetService.reset(OrganisationCode, ServiceName.BVN_AGENT_MANAGEMENT);
    }

}
