package ng.upperlink.nibss.cmms.util;

import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.service.FormValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Validates Account Number
 */
@Service
public class AccountLookUp {
    private FormValidation formValidation;

    @Autowired
    public void setFormValidation(FormValidation formValidation) {
        this.formValidation = formValidation;
    }

    public boolean validateAccount(String value, int digits) {
        return !this.formValidation.validNumber(value) ? false : this.formValidation.validLength(value, digits);
    }

    public String validateAccount(String accNumber, String bvn) {
        if (!this.validateAccount(accNumber, Constants.ACC_NUMBER_MAX_DIGITS)) {
            return String.format("Account number must be digits and exactly %d characters", Constants.ACC_NUMBER_MAX_DIGITS);
        } else {
            return !this.validateAccount(bvn, Constants.BVN_MAX_DIGITS) ? String.format("BVN must be digits and exactly %d characters", Constants.BVN_MAX_DIGITS) : null;
        }
    }

    public String validateAccount(String bvn) {
        return !this.validateAccount(bvn, Constants.BVN_MAX_DIGITS) ? String.format("BVN must be digits and exactly %d characters", Constants.BVN_MAX_DIGITS) : null;

    }

    public boolean performAccountNameLookUp(String bvn) {
        return false;
    }
}
