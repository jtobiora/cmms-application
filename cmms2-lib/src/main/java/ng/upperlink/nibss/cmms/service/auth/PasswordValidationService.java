/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service.auth;

import ng.upperlink.nibss.cmms.dto.auth.PasswordPolicyResponse;
import ng.upperlink.nibss.cmms.enums.PasswordPolicyCodes;
import ng.upperlink.nibss.cmms.model.auth.PasswordLog;
import ng.upperlink.nibss.cmms.model.auth.PasswordPolicySetting;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.repo.auth.PasswordLogRepo;
import ng.upperlink.nibss.cmms.repo.auth.PasswordPolicySettingRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 *
 * @author cmegafu
 */
@Service
@Scope("prototype")
@Slf4j
public class PasswordValidationService implements PasswordPolicyCodes {
    
    @Autowired
    private PasswordPolicySettingRepo passwordPolicyRepo;
    @Autowired
    private PasswordLogRepo passwordLogRepo;
    @Autowired 
    private UserService userService;
    
    @Value("${encryption.salt}")
    private String salt;
    
    static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9]*");
    static final Pattern SMALL_LETTER_PATTERNS = Pattern.compile("[a-z]");
    static final Pattern CAPITAL_LETTER_PATTERNS = Pattern.compile("[A-Z]");
    static final Pattern NUMBER_PATTERNS = Pattern.compile("[0-9]");

    @Override
    public boolean isPwdEqualToUsername(String username, String password) {
        return username.trim().equalsIgnoreCase(password);
    }

    @Override
    public boolean isUsernameInverted(String username, String password) {
        String reverse = "";
        // reverse the username
        for(int x = username.length() -1; x >= 0; x--){
            reverse = reverse + username.charAt(x);
        }
        return reverse.trim().equalsIgnoreCase(password);
    }

    @Override
    public boolean isUsernameCapitalized(String username, String password) {
        return username.trim().toUpperCase().equals(password);
    }

    @Override
    public boolean isUsernameDuplicated(String username, String password) {
        return username.trim().equalsIgnoreCase(password);
    }

    @Override
    public boolean isUsernameHavingAdditionalDigits(String username, String password) {
        return password.trim().contains(username);
    }

    @Override
    public boolean isUsernameInPassword(String username, String password) {
        if (password.toLowerCase().contains(username.toLowerCase())){
            return true;
        }

        return false;
    }

    @Override
    public boolean isPasswordContainingUsualWord(String username, String password) {

        if (password.toLowerCase().contains("admin") || password.toLowerCase().contains("user") || password.toLowerCase().contains("password")){
            return true;
        }

        return false;
    }

    @Override
    public boolean isPasswordDifferentFrmPersonalData(String password) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isPasswordDifferentFrmRegularWords(String password) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isNotMinLength(String password) {
        return password.length() < policySettings().getMinimumLength();
    }

    @Override
    public boolean isNotMaxLength(String password) {
        return password.length() > policySettings().getMaximumLength();
    }

    @Override
    public boolean isNotPassMixOfCharactters(String password) {
        return !containSmallLetters(password) || !containsSpecialCharacters(password) || !containCapitalLetters(password) || !containNumbers(password);
    }

    @Override
    public boolean isPastPassword(String username, String password) {
        User user = userService.getByEmail(username);
        if (null == user)
            return false;
        return null != findUserPassword(user, password);
    }

    @Override
    public boolean isEmptyUsername(String username) {
        return (null == username || username.trim().isEmpty());
    }

    @Override
    public boolean isEmptyPassword(String password) {
        return (null == password || password.trim().isEmpty());
    }
    
    public PasswordPolicyResponse isValid(String username, String password) {
        // check if the user name if empty
        if (isEmptyUsername(username)) {
            return response(PasswordPolicyCodes.USERNAME_EMPTY, PasswordPolicyCodes.USERNAME_EMPTY_MSG);
        }
        
        // check if the password is empty
        if (isEmptyPassword(password)) {
            return response(PasswordPolicyCodes.PASS_EMPTY, PasswordPolicyCodes.PASS_EMPTY_MSG);
        }
        
        // check if the password is equal to username
        if (isPwdEqualToUsername(username, password)) {
            return response(PasswordPolicyCodes.PASS_EMPTY, PasswordPolicyCodes.PASS_EMPTY_MSG);
        }
        
        // check if the username is inverted
        if (isUsernameInverted(username, password)) {
            return response(PasswordPolicyCodes.USERNAME_INVERTED, PasswordPolicyCodes.USERNAME_INVERTED_MSG);
        }
        
        // check if the username is capitalized
        if (isUsernameCapitalized(username, password)) {
            return response(PasswordPolicyCodes.USERNAME_CAPITALIZED, PasswordPolicyCodes.USERNAME_CAPITALIZED_MSG);
        }
        
        // check if the username is duplicated
        if (isUsernameDuplicated(username, password)) {
            return response(PasswordPolicyCodes.USERNAME_CAPITALIZED, PasswordPolicyCodes.USERNAME_CAPITALIZED_MSG);
        }
        
        // check if the password is having additional digits
        if (isUsernameHavingAdditionalDigits(username, password)) {
            return response(PasswordPolicyCodes.USERNAME_HAVING_ADDITIONAL_DIGITS, PasswordPolicyCodes.USERNAME_HAVING_ADDITIONAL_DIGITS_MSG);
        }
        
        // Minimum length 
        if (isNotMinLength(password)) {
            return response(PasswordPolicyCodes.MIN_LENGTH, resolveString("MIN_PASS", String.valueOf(policySettings().getMinimumLength()), PasswordPolicyCodes.MIN_LENGTH_MSG));
        }
        
        // Maximum Length
        if (isNotMaxLength(password)) {
            return response(PasswordPolicyCodes.MAX_LENGTH, resolveString("MAX_PASS", String.valueOf(policySettings().getMaximumLength()), PasswordPolicyCodes.MAX_LENGTH_MSG));
        }
        
        // Mix of characters
        if (isNotPassMixOfCharactters(password)) {
            return response(PasswordPolicyCodes.PASS_MIX_OF_CHARACTERS, PasswordPolicyCodes.PASS_MIX_OF_CHARACTERS_MSG);
        }
        
        // Past password
        if (isPastPassword(username, password)) {
            return response(PasswordPolicyCodes.PAST_PASSWORD, PasswordPolicyCodes.PAST_PASSWORD_MSG);
        }

        //Username in password
        if (isUsernameInPassword(username,password)){
            return response(PasswordPolicyCodes.PASSWORD_CONTAIN_USERNAME, PasswordPolicyCodes.USERNAME_IN_PASSWORD);
        }

        //ensure that admin, user and password is not in password
        if (isUsernameInPassword(username,password)){
            return response(PasswordPolicyCodes.PASSWORD_CONTAIN_USUAL_WORD, PasswordPolicyCodes.PASSWORD_CONTAINS_USUAL_WORD);
        }

        return response(PasswordPolicyCodes.VALID, PasswordPolicyCodes.VALID_MSG);
    }
    
    public PasswordPolicySetting policySettings() {
        try {
            List<PasswordPolicySetting> passwordPolicySettings = passwordPolicyRepo.getPolicySetting();
            if (null != passwordPolicySettings && !passwordPolicySettings.isEmpty()) {
                return passwordPolicySettings.get(0);
            }else {
                PasswordPolicySetting passwordPolicySetting = new PasswordPolicySetting();
                passwordPolicySetting.setMaxAllowedPassword(6);
                passwordPolicySetting.setMaximumLength(15);
                passwordPolicySetting.setMinimumLength(8);
                passwordPolicySetting.setUpdatePeriod(30);
                return passwordPolicyRepo.save(passwordPolicySetting);
            }
        } catch (Exception e) {
            log.error("Unable to get password policy setting ...", e);
        }
        return null;
    }
    
    // update password policy
    public PasswordPolicySetting updatePasswordPolicySetting(PasswordPolicySetting passwordPolicySetting) {
         try {
            passwordPolicySetting.setUpdatedAt(new Date());
            return passwordPolicyRepo.saveAndFlush(passwordPolicySetting);
        } catch (Exception e) {
            log.error("Unable to update password policy setting", e);
        }
         return null;
    }
    
    private PasswordLog findUserPassword(User user, String password) {
        try {
            return passwordLogRepo.findPassword(user, EncyptionUtil.doSHA512Encryption(password, salt));
        } catch (Exception e) {
            log.error("Unable to find user with password", e);
        }
        return null;
    }
    
    // get all password logs for a user
    public List<PasswordLog> getUserPasswordLogs(User user) {
        try {
            return passwordLogRepo.getUserPasswordLogs(user, new Sort(Sort.Direction.ASC, "id"));
        } catch (Exception e) {
            log.error("Unable to fetch all user password logs", e);
        }
        return new ArrayList<>();
    }
    
    public PasswordLog savePasswordLog(String encryptedPassword, User user) {
        try {
            PasswordLog passwordLog = new PasswordLog();
            passwordLog.setUser(user);
            passwordLog.setPassword(encryptedPassword);
            passwordLog.setCreatedAt(new Date());
            passwordLog.setUpdatedAt(new Date());
            return passwordLogRepo.saveAndFlush(passwordLog);
        } catch (Exception e) {
            log.error("Unable to save password log", e);
        }
        return null;
    }
    
    public PasswordLog updatePasswordLog(PasswordLog passwordLog, String encryptedPassword) {
        try {
            passwordLog.setCreatedAt(new Date());
            passwordLog.setUpdatedAt(new Date());
            passwordLog.setPassword(encryptedPassword);
            return passwordLogRepo.saveAndFlush(passwordLog);
        } catch (Exception e) {
            log.error("Unable to update password log {}", passwordLog, e);
        }
        return null;
    }

    private boolean containsSpecialCharacters(String password) {
        return !PATTERN.matcher(password).matches();
    }
    
    private boolean containSmallLetters(String password) {
        return SMALL_LETTER_PATTERNS.matcher(password).find();
    }
    
    private boolean containCapitalLetters(String password) {
        return CAPITAL_LETTER_PATTERNS.matcher(password).find();
    }
    
    private boolean containNumbers(String password) {
        return NUMBER_PATTERNS.matcher(password).find();
    }
    
    private PasswordPolicyResponse response(String responseCode, String responseMessage) {
        return new PasswordPolicyResponse(responseMessage, responseCode);
    }
    
    private String resolveString(String key, String value, String text) {
        Map valuesMap = new HashMap();
        valuesMap.put(key, value);
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        return sub.replace(text);
    }
}
