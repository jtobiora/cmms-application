package ng.upperlink.nibss.cmms.util;

import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import org.apache.commons.validator.routines.EmailValidator;

import javax.swing.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;

public class DeleteSoon {

    //validate Demographics here
    public String validateDemography(Date dateOfBirth, String firstName, Object gender, Object maritalStatus,
                                     String middleName, boolean isNigeria, boolean isOtherNation,
                                     Object otherNationality, Object stateOfOrigin, String surname, Object title, Object lga) throws CMMSException {

        if (!validData(title)) {
            throw new CMMSException("title is required","400","400");
        }

        if (!validData(surname)) {
            throw new CMMSException("surname is required","400","400");
        }
        if (!validName(surname)) {
            throw new CMMSException("surname is invalid, Name must be between 2 and 255 non-digit characters","400","400");
        }

        if (!validData(firstName)) {
            throw new CMMSException("first name is required","400","400") ;
        }
        if (!validName(firstName)) {
            throw new CMMSException( "first name is invalid, Name must be between 2 and 255 non-digit characters","400","400");
        }

        if (validData(middleName)) {
            if (!validName(middleName)) {
                throw new CMMSException( "middle name is invalid, Name must be between 2 and 255 non-digit characters","400","400");
            }
        }

        if (!validData(dateOfBirth)) {
            throw new CMMSException( "date of birth is required","400","400");
        }
        if (!validDateOfBirth(dateOfBirth)) {
            throw new CMMSException( "Date of birth is invalid. Age must be between 16 - 150 years","400","400");
        }

        if (!validData(gender)) {
            throw new CMMSException( "gender is required","400","400");
        }

        if (!validData(maritalStatus)) {
            throw new CMMSException( "marital status is required","400","400");
        }

        if (isNigeria) {
            if (!validData(stateOfOrigin)) {
                throw new CMMSException( "state of origin is required","400","400");
            }
            if (!validData(lga)) {
                throw new CMMSException( "lga is required","400","400");
            }
        }

        if (isOtherNation) {
            if (!validData(otherNationality)) {
                throw new CMMSException( "Country is required","400","400");
            }
        }

        return null;
    }

    public String validateBankInformation(Object stateOfCapture, Object lgaOfCapture, String nin, String tin, String remark) throws CMMSException {

        if (!validData(stateOfCapture)) {
            throw new CMMSException( "state Of capture is required","400","400");
        }
        if (!validData(lgaOfCapture)) {
            return "local government area of capture is required";
        }

        if (validData(nin)) {
            if (!validNumber(nin) || !validLength(nin, 11, 11)) {
                throw new CMMSException( "Invalid NIN, NIN must be of 11 digits only","400","400");
            }
        }
        if (validData(tin)) {
            String newTin = tin.replace("-", "");
            if (!validNumber(newTin) || !validLength(newTin, 6, 9)) {
                throw new CMMSException( "Invalid TIN, TIN must be of 6 - 9 digits only","400","400");
            }

            if (tin.equals(nin)) {
                throw new CMMSException( "TIN and NIN cannot be same","400","400");
            }
        }

        if (validData(remark)) {
            if (!validLength(remark, 20, 255)) {
                throw new CMMSException( "Invalid Remark, Remark must be between 20 and 255 characters","400","400");
            }
        }

        return null;
    }

    public String validateContactDetails(String email, String residentialAddress, boolean isNigeria, boolean isOtherNation,
                                         Object otherNationality, Object stateOfResidence,
                                         Object lgaOfResidence, String landMark, String phoneNumber1, String phoneNumber2) throws CMMSException {

        if (validData(email)) {
            if(email.contains(" ")){
                throw new CMMSException( "Please ensure you remove all whitespaces in the emailAddress address provided","400","400");
            }

            if (!validEmail(email)){
                throw new CMMSException( "Email address is invalid. Please provide a valid emailAddress address","400","400");
            }
        }

        if (!validData(residentialAddress)) {
            throw new CMMSException( "residential address is required","400","400");
        }
        if (!validLength(residentialAddress, 20, 255)) {
            throw new CMMSException( "Invalid Residential Address, Residential Address must be between 20 and 255 characters","400","400");
        }

        if (isNigeria) {
            if (!validData(stateOfResidence)) {
                throw new CMMSException( "state Of residence is required","400","400");
            }
            if (!validData(lgaOfResidence)) {
                throw new CMMSException( "local government area Of resident is required","400","400");
            }
        }

        if (isOtherNation) {
            if (!validData(otherNationality)) {
                throw new CMMSException( "Other Country is required","400","400");
            }
        }

        if (isOtherNation) {
            if (!validData(landMark)) {
                throw new CMMSException( "landmark is required","400","400");
            }
        }

        if (!validData(phoneNumber1)) {
            throw new CMMSException( "phoneNumber1 is required","400","400");
        }
        if (!validPhoneNumber(phoneNumber1)) {
            throw new CMMSException( "Invalid phoneNumber1, Phone number must be between 7 and 15 digit characters","400","400");
        }

        if (validData(phoneNumber2)) {
            if (!validPhoneNumber(phoneNumber2)) {
                throw new CMMSException( "Invalid phoneNumber2, Phone number must be between 7 and 15 digit characters","400","400");
            }
        }

        return null;
    }

    public String validateSupplementaryInformation(String addInfo1, String addInfo2, String addInfo3, String addInfo4) throws CMMSException {

        if (validData(addInfo1)) {
            if (!validLength(addInfo1, 20, 180)) {
                throw new CMMSException( "Invalid addition information 1, addition information must be between 20 and 180 characters","400","400");
            }
        }

        if (validData(addInfo2)) {
            if (!validLength(addInfo2, 20, 180)) {
                throw new CMMSException( "Invalid addition information 2, addition information must be between 20 and 180 characters","400","400");
            }
        }

        if (validData(addInfo3)) {
            if (!validLength(addInfo3, 20, 180)) {
                throw new CMMSException( "Invalid addition information 3, addition information must be between 20 and 180 characters","400","400");
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

    public boolean validName(String name) throws CMMSException {

        if (name.length() < 2) {
            throw new CMMSException(Errors.LENGTH_TOO_SHORT.getValue().replace("{}","2"),"400","400");
        }

        if (name.length() > 255) {
            throw new CMMSException(Errors.LENGTH_TOO_LONG.getValue().replace("{}","255"),"400","400");
        }

        //pure alphabets
        if (!(Pattern.compile("[a-zA-Z]+").matcher(name).matches())) {
            throw new CMMSException(Errors.INVALID_DATA_PROVIDED.getValue().replace("{}",name),"400","400");
        }

        return true;
    }

    public boolean validNumber(String numbers) throws CMMSException {

        //pure number
        if (!(Pattern.compile("[0-9]+").matcher(numbers).matches())) {
            throw new CMMSException(Errors.INVALID_DATA_PROVIDED.getValue().replace("{}",numbers),"400","400");
        }
        return true;
    }

    public boolean validLength(String string, int min, int max) throws CMMSException {

        if (string.length() < min) {
            throw new CMMSException(Errors.LENGTH_TOO_SHORT.getValue().replace("{}",String.valueOf(min)),"400","400");
        }

        if (max != 0) {
            if (string.length() > max) {
                throw new CMMSException(Errors.LENGTH_TOO_LONG.getValue().replace("{}",String.valueOf(max)),"400","400");
            }
        }

        return true;
    }

    public boolean validLength(String string, int digits) throws CMMSException {

        if (string.length() < digits) {
            throw new CMMSException(Errors.LENGTH_TOO_SHORT.getValue().replace("{}",String.valueOf(digits)),"400","400");
        }

        if (digits != 0) {
            if (string.length() > digits) {
                throw new CMMSException(Errors.LENGTH_TOO_LONG.getValue().replace("{}",String.valueOf(digits)),"400","400");
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

    public boolean validEmail(String emailAddress) throws CMMSException {

        if (emailAddress.contains("^")) {

            throw new CMMSException(Errors.INVALID_EMAIL_PROVIDED.getValue().replace("{}",emailAddress),"400","400");
        }

        if (Pattern.compile("[^a-zA-Z0-9@._]").matcher(emailAddress.replace("-", "")).find()) {
            throw new CMMSException(Errors.INVALID_EMAIL_PROVIDED.getValue().replace("{}",emailAddress),"400","400");
        }

        return EmailValidator.getInstance().isValid(emailAddress);
    }
}
