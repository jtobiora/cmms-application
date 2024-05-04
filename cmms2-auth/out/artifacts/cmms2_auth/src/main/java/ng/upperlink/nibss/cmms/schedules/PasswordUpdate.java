/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.schedules;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.service.PasswordRecoveryCodeService;
import ng.upperlink.nibss.cmms.service.PasswordValidationService;
import ng.upperlink.nibss.cmms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author cmegafu
 */
@Slf4j
@Component
public class PasswordUpdate {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordRecoveryCodeService passwordRecoveryCodeService;
    
    @Autowired
    private PasswordValidationService passwordValidationService;
    
    @Scheduled(cron = "${password.update.period}")
    public void updatePassword() {
        // get all the users
        userService.getAllUsersForPasswordUpdate().forEach((u) -> {
            // check if this user's password has expired
            if (passwordRecoveryCodeService.checkValidityPeriod(u.getPasswordUpdatedAt()) >= passwordValidationService.policySettings().getUpdatePeriod()) {
                // set change password to true
                u.setChange_password(true);
                if (null == userService.save(u))
                   log.error("Unable to flag a user for password update.");
                // send a mail to the user requesting for password update
                userService.passwordUpdateNotification(u.getEmailAddress());
            }
        });
    }
}
