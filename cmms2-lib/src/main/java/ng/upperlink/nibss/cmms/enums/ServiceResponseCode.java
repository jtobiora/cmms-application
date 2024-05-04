/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.enums;

public interface ServiceResponseCode {
    /**
     * Response Codes
     * 
     */
    public String VALID = "00";
    public String USER_DOES_EXIST = "14";
    public String PASSWORD_RECOVERY_CODE_ERROR = "15";
    public String RECOVERY_CODE_DOES_NOT_EXIST = "16";
    public String RECOVERY_CODE_EXPIRED = "17";
    public String UNABLE_TO_UPDATE_PASSWORD = "18";
    public String PASSWORD_DOES_NOT_MATCH = "19";
    public String UNABLE_TO_UPDATE_PASSWORD_LOG = "20";
    public String UNABLE_TO_UPDATE_USER_PASSWORD = "21";
    public String RECOVERY_CODE_USED = "22";
    
    /**
     * Response Messages
     * 
     */
    public String USER_DOES_EXIST_MSG = "This user does not exist";
    public String PASSWORD_RECOVERY_CODE_ERROR_MSG = "Something went wrong while trying to generateAuthRequest password recovery code, please try again.";
    public String PASSWORD_RESET_SUCCESSFUL = "Password successfully reset, kindly check your emailAddress to continue";
    public String RECOVERY_CODE_DOES_NOT_EXIST_MSG = "This recovery code does not exist.";
    public String RECOVERY_CODE_EXPIRED_MSG = "This code has exceeded the allowed 24 hours period. Kindly initiate another password reset.";
    public String RECOVERY_CODE_VALID = "Valid Recovery code";
    public String PASSWORD_UPDATED = "Password succesfully updated";
    public String UNABLE_TO_UPDATE_PASSWORD_MSG = "Something went wrong while trying to update user details, please try again.";
    public String PASSWORD_DOES_NOT_MATCH_MSG = "Your previous password is incorrect";
    public String UNABLE_TO_UPDATE_PASSWORD_LOG_MSG = "Something went wrong while trying to log user's password, please try again.";
    public String UNABLE_TO_UPDATE_USER_PASSWORD_MSG = "Something went wrong while trying to update user details, please try again";
    public String RECOVERY_CODE_USED_MSG = "This code has been used, kindly initiate another recovery process to recover your password.";
}
