/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service.auth;

import ng.upperlink.nibss.cmms.model.auth.PasswordRecoveryCode;
import ng.upperlink.nibss.cmms.model.User;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.repo.auth.PasswordRecoveryCodeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Slf4j
public class PasswordRecoveryCodeService {

    @Autowired
    private PasswordRecoveryCodeRepo passwordRecoveryCodeRepo;
    
    public PasswordRecoveryCode save(String recoveryCode, User user) {
        try {
            PasswordRecoveryCode passwordRecoveryCode = new PasswordRecoveryCode();
            passwordRecoveryCode.setRecoveryCode(recoveryCode);
            passwordRecoveryCode.setUser(user);
            return passwordRecoveryCodeRepo.saveAndFlush(passwordRecoveryCode);
        } catch (Exception e) {
            log.error("Unable to save password recovery code", e);
        }
        return null;
    }
    
    public PasswordRecoveryCode findRecoveryCode(String recoveryCode) {
        try {
            return passwordRecoveryCodeRepo.findRecoveryCode(recoveryCode);
        } catch (Exception e) {
            log.error("Unable to find recovery code {}", recoveryCode, e);
        }
        return null;
    }
    
    public long checkValidityPeriod(Date createdDate) {
        long diffInMillies = Math.abs(new Date().getTime() - createdDate.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
    
    public void updateRecoveryCode(PasswordRecoveryCode passwordRecoveryCode) {
        try {
            passwordRecoveryCode.setStatus(true);
            passwordRecoveryCode.setUpdatedAt(new Date());
            passwordRecoveryCodeRepo.saveAndFlush(passwordRecoveryCode);
        } catch (Exception e) {
            log.error("Unable to update password recovery code {}", passwordRecoveryCode, e);
        }
    }
}
