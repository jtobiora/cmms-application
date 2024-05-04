package ng.upperlink.nibss.cmms.service;

import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;


@Service
public class FormValidation {


    //validate Demographics here
    public String validateDemography(Date dateOfBirth, String firstName, Object gender, Object maritalStatus,
                                     String middleName, boolean isNigeria, boolean isOtherNation,
                                     Object otherNationality, Object stateOfOrigin, String surname, Object title, Object lga) {

        if (!validData(title)) {
            return "title is required";
        }

        if (!validData(surname)) {
            return "surname is required";
        }
        if (!validName(surname)) {
            return "surname is invalid, Name must be between 2 and 255 non-digit characters";
        }

        if (!validData(firstName)) {
            return "first name is required";
        }
        if (!validName(firstName)) {
            return "first name is invalid, Name must be between 2 and 255 non-digit characters";
        }

        if (validData(middleName)) {
            if (!validName(middleName)) {
                return "middle name is invalid, Name must be between 2 and 255 non-digit characters";
            }
        }

        if (!validData(dateOfBirth)) {
            return "date of birth is required";
        }
        if (!validDateOfBirth(dateOfBirth)) {
            return "Date of birth is invalid. Age must be between 16 - 150 years";
        }

        if (!validData(gender)) {
            return "gender is required";
        }

        if (!validData(maritalStatus)) {
            return "marital status is required";
        }

        if (isNigeria) {
            if (!validData(stateOfOrigin)) {
                return "state of origin is required";
            }
            if (!validData(lga)) {
                return "lga is required";
            }
        }

        if (isOtherNation) {
            if (!validData(otherNationality)) {
                return "Country is required";
            }
        }

        return null;
    }

    public String validateBankInformation(Object stateOfCapture, Object lgaOfCapture, String nin, String tin, String remark) {

        if (!validData(stateOfCapture)) {
            return "state Of capture is required";
        }
        if (!validData(lgaOfCapture)) {
            return "local government area of capture is required";
        }

        if (validData(nin)) {
            if (!validNumber(nin) || !validLength(nin, 11, 11)) {
                return "Invalid NIN, NIN must be of 11 digits only";
            }
        }
        if (validData(tin)) {
            String newTin = tin.replace("-", "");
            if (!validNumber(newTin) || !validLength(newTin, 6, 9)) {
                return "Invalid TIN, TIN must be of 6 - 9 digits only";
            }

            if (tin.equals(nin)) {
                return "TIN and NIN cannot be same";
            }
        }

        if (validData(remark)) {
            if (!validLength(remark, 20, 255)) {
                return "Invalid Remark, Remark must be between 20 and 255 characters";
            }
        }

        return null;
    }

    public String validateContactDetails(String email, String residentialAddress, boolean isNigeria, boolean isOtherNation,
                                         Object otherNationality, Object stateOfResidence,
                                         Object lgaOfResidence, String landMark, String phoneNumber1, String phoneNumber2) {

        if (validData(email)) {
     if(email.contains(" ")){
                return "Please ensure you remove all whitespaces in the emailAddress address provided";
            }

            if (!validEmail(email)){
                return "Email address is invalid. Please provide a valid emailAddress address";
            }
        }

        if (!validData(residentialAddress)) {
            return "residential address is required";
        }
        if (!validLength(residentialAddress, 20, 255)) {
            return "Invalid Residential Address, Residential Address must be between 20 and 255 characters";
        }

        if (isNigeria) {
            if (!validData(stateOfResidence)) {
                return "state Of residence is required";
            }
            if (!validData(lgaOfResidence)) {
                return "local government area Of resident is required";
            }
        }

        if (isOtherNation) {
            if (!validData(otherNationality)) {
                return "Other Country is required";
            }
        }

        if (isOtherNation) {
            if (!validData(landMark)) {
                return "landmark is required";
            }
        }

        if (!validData(phoneNumber1)) {
            return "phoneNumber1 is required";
        }
        if (!validPhoneNumber(phoneNumber1)) {
            return "Invalid phoneNumber1, Phone number must be between 7 and 15 digit characters";
        }

        if (validData(phoneNumber2)) {
            if (!validPhoneNumber(phoneNumber2)) {
                return "Invalid phoneNumber2, Phone number must be between 7 and 15 digit characters";
            }
        }

        return null;
    }

    public String validateSupplementaryInformation(String addInfo1, String addInfo2, String addInfo3, String addInfo4) {

        if (validData(addInfo1)) {
            if (!validLength(addInfo1, 20, 180)) {
                return "Invalid addition information 1, addition information must be between 20 and 180 characters";
            }
        }

        if (validData(addInfo2)) {
            if (!validLength(addInfo2, 20, 180)) {
                return "Invalid addition information 2, addition information must be between 20 and 180 characters";
            }
        }

        if (validData(addInfo3)) {
            if (!validLength(addInfo3, 20, 180)) {
                return "Invalid addition information 3, addition information must be between 20 and 180 characters";
            }
        }

        if (validData(addInfo4)) {
            if (!validLength(addInfo4, 20, 180)) {
                return "Invalid addition information 4, addition information must be between 20 and 180 characters";
            }
        }

        return null;
    }

    public void displayErrorMessage(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog("Validation Error");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);

//        JOptionPane.showMessageDialog(null,message,"Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean validData(Object object) {

        if (object == null || String.valueOf(object).isEmpty()) {
            return false;
        }

        return true;
    }

//    public static void main(String[] args){
//        System.out.println(validBVN("1234567890"));
//    }

    public static boolean validBVN(String bvn) {

        if (bvn.matches("^\\d{11}$")) {
            return true;
        }

        return false;
    }

    public boolean validPhoneNumber(String phoneNumber) {

        if (phoneNumber.matches("^\\d{7,15}$")) {
            return true;
        }

//        if (phoneNumber.matches("^+\\d{13}$")){
//            return true;
//        }

        return false;
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


        LocalDate dt = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dt, currentDate);

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
