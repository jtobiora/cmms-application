package ng.upperlink.nibss.cmms.controller.account;

import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.account.request.AccountRequest;
import ng.upperlink.nibss.cmms.dto.account.response.AccountResponse;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.account.AccountValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;

@RestController
@RequestMapping("/account")
public class AccountValidation {
    private static Logger LOG = LoggerFactory.getLogger(AccountValidation.class);
    private AccountValidationService accountValidationService;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAccountValidationService(AccountValidationService accountValidationService) {
        this.accountValidationService = accountValidationService;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody AccountRequest request,
                                                  @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail)
    {
        User user = userService.get(userDetail.getUserId());
        if (user == null)
        {
            return null;
        }
        try {
            return ResponseEntity.ok(accountValidationService.request(request));
        } catch (CMMSException e) {
            e.printStackTrace();
            LOG.error("Account Validation error",e);
            return ErrorDetails.setUpErrors("Account Validation error", Arrays.asList(e.getMessage()),e.getCode());
        }
    }
}
