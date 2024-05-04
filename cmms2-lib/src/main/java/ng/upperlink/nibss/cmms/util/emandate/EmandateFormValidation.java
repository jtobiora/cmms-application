package ng.upperlink.nibss.cmms.util.emandate;

import ng.upperlink.nibss.cmms.service.account.AccountValidationService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;


@Service
public class EmandateFormValidation {
    @Value("${cmms.fee}")
    private BigDecimal cmmsFee;
    private AccountValidationService accountValidationService;
    @Autowired
    public void setAccountValidationService(AccountValidationService accountValidationService) {
        this.accountValidationService = accountValidationService;
    }

    //validate Demographics here
    public String validateRate(boolean fixedAmountMandate, BigDecimal amount, Integer frequency,
                               String narration,Long productId) {

        if (!validAmount(amount)) {
            return "Amount must be greater than the transaction fee: "+cmmsFee;
        }
        if (fixedAmountMandate) {
            if (!validData(frequency) && frequency<=0)
                return "Frequency must be greater than zero for fixed mandate ";
        }

        if (!validData(narration)) {
            return "Mandate narration is required";
        }
        if (!validLength(narration,5,255)) {
            return "mandate narration is invalid, Narration must be between 5 and 255  characters";
        }
        return null;
    }

    public String validateBankInformation(String accountNumber,String bankCode) {

        if (!validAccountNumber(accountNumber)) {
            return "Invalid account number, Account number must be of 10 digits only";
        }

        if (validLength(bankCode,3,3)) {
            if (!validNumber(bankCode) || !validLength(bankCode, 3)) {
                return "Invalid Bank code, Bank code must be of 3 digits only";
            }
        }

        return null;
    }

    public String validateContactDetails(String phoneNumber,String email,String payerName,String payerAddress) {

        if (validData(email)) {
     if(email.contains(" ")){
                return "Invalid email address: Please ensure you remove all whitespaces in the emailAddress address provided";
            }

            if (!validEmail(email)){
                return "Email address is invalid. Please provide a valid emailAddress address";
            }
        }

        if (!validData(payerAddress)) {
            return "Residential address is required";
        }
        if (!validLength(payerAddress, 10, 100)) {
            return "Invalid Residential Address. Residential Address must be between 10 and 100 characters";
        }


        if (!validLength(payerName,2,100)) {
            return "Subscriber's name is invalid, Name must be between 2 and 255 non-digit characters";
        }
        if (!validPhoneNumber(phoneNumber)) {
            return "Invalid phoneNumber. Phone number must be between 11 and 15 digit characters";
        }

        return null;
    }

    public String validateRequestCodes(String channelCode,String subscriberCode,String subscriberPassCode,boolean isBiller) {

        if (!validNumber(channelCode))
            return "Invalid channel code : "+channelCode;
        if (!validLength(channelCode, 2, 2)) {
            return "Channel code, must be two digits";
        }

        if (!validData(subscriberCode)) {
           return "Subscriber's code cannot be null";
        }
        if (!validLength(subscriberCode, 2, 50)) {
            return "Invalid subscriber's code, Subscriber's code must be between 2 and 50 characters";
        }
        if (isBiller) {
            if (!validData(subscriberPassCode))
                return "Required pass code from Financial Institution";
            if (!validLength(subscriberPassCode, 4, 4))
                return "Invalid Pin, pin must be 4 digits";
        }
        return null;
    }
    public boolean validData(Object object) {

        if (object == null || String.valueOf(object).isEmpty()) {
            return false;
        }

        return true;
    }
    public boolean validAmount(BigDecimal amount) {

        if (amount == null|| amount.compareTo(cmmsFee) !=1) {
            return false;
        }
        return true;
    }

    public boolean validBVN(String bvn) {
        if (!validNumber(bvn))
            return false;
        if (!validLength(bvn,11))
            return false;
        else
            return true;
    }

    public boolean validPhoneNumber(String phoneNumber) {

        if (!validLength(phoneNumber,11,15))
            return false;
        if (!validNumber(phoneNumber))
            return false;
        else return true;
    }
    public boolean validAccountNumber(String accountNumber) {
        if (!validLength(accountNumber,10,10))
            return false;
        if ((!validNumber(accountNumber)))
            return false;
        else
            return true;
    }

    public boolean validName(String name) {

        if (name.length() < 2) {
            return false;
        }

        if (name.length() > 255) {
            return false;
        }

        //pure alphabets
        if (!(Pattern.compile("[a-zA-Z]+").matcher(name).matches())) {
            return false;
        }

        return true;
    }

    public boolean validNumber(String numbers) {

        //pure number
        if (!(Pattern.compile("[0-9]+").matcher(numbers).matches())) {
            return false;
        }

        return true;
    }

    public boolean validLength(String string, int min, int max) {

        if (string.length() < min) {
            return false;
        }

        if (max != 0) {
            if (string.length() > max) {
                return false;
            }
        }

        return true;
    }

    public boolean validLength(String string, int digits) {

        if (string.length() < digits) {
            return false;
        }

        if (digits != 0) {
            if (string.length() > digits) {
                return false;
            }
        }
        return true;
    }

    public boolean validDateOfBirth(Date dob) {

        Date today = new Date();

        if (dob.after(today)) {
            return false;
        }


        LocalDate localDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(localDate, currentDate);

        if (period.getYears() < 16) { //must not be less than 16 years
            return false;
        }

        if (period.getYears() > 150) { //must be greater than 150 years
            return false;
        }
        return true;
    }

    public boolean validEmail(String emailAddress) {

        if (emailAddress.contains("^")) {
            return false;
        }

        if (Pattern.compile("[^a-zA-Z0-9@._]").matcher(emailAddress.replace("-", "")).find()) {
            return false;
        }

        return EmailValidator.getInstance().isValid(emailAddress);
    }

}
