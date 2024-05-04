/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.enums;


public interface PasswordPolicyCodes {
    public boolean isEmptyUsername(String username);
    public boolean isEmptyPassword(String password);
    public boolean isPwdEqualToUsername(String username, String password);
    public boolean isUsernameInverted(String username, String password);
    public boolean isUsernameCapitalized(String username, String password);
    public boolean isUsernameDuplicated(String username, String password);
    public boolean isUsernameHavingAdditionalDigits(String username, String password);
    public boolean isUsernameInPassword(String username, String password);
    public boolean isPasswordContainingUsualWord(String username, String password);
    public boolean isPasswordDifferentFrmPersonalData(String password);
    public boolean isPasswordDifferentFrmRegularWords(String password);
    public boolean isNotMinLength(String password);
    public boolean isNotMaxLength(String password);
    public boolean isNotPassMixOfCharactters(String password);
    public boolean isPastPassword(String username, String password);
    
    /**
     * Error Codes
     */
    public String VALID = "00";
    public String PASSWORD_EQUAL_TO_USERNAME = "01";
    public String USERNAME_INVERTED = "02";
    public String USERNAME_CAPITALIZED = "03";
    public String USERNAME_DUPLICATED = "04";
    public String USERNAME_HAVING_ADDITIONAL_DIGITS = "05";
    public String PASS_DIFFERENT_FROM_PERSONAL_DATA = "06";
    public String PASS_DIFFERENT_FROM_REGULAR_WORDS = "07";
    public String MIN_LENGTH = "08";
    public String MAX_LENGTH = "09";
    public String PASS_MIX_OF_CHARACTERS = "10";
    public String PAST_PASSWORD = "11";
    public String PASS_EMPTY = "12";
    public String USERNAME_EMPTY = "13";
    public String INVALID_REQUEST = "14";
    public String PASSWORD_CONTAIN_USERNAME = "15";
    public String PASSWORD_CONTAIN_USUAL_WORD = "16";

    /**
     * Error Messages
     * 
     */
    public String PASSWORD_EQUAL_TO_USERNAME_MSG = "Your password is equal to your username.";
    public String USERNAME_INVERTED_MSG = "Your password is an inversion of your username";
    public String USERNAME_CAPITALIZED_MSG = "Your password is a capitalization of your username";
    public String USERNAME_DUPLICATED_MSG = "Your password is a duplication of your username";
    public String USERNAME_HAVING_ADDITIONAL_DIGITS_MSG = "Your password should not be an addition of extra characters to your username";
    public String PASS_DIFFERENT_FROM_PERSONAL_DATA_MSG = "Your password must be different from your personal data";
    public String PASS_DIFFERENT_FROM_REGULAR_WORDS_MSG = "Your password must be different from dictionary words";
    public String MIN_LENGTH_MSG = "Your password must not be less than ${MIN_PASS} characters.";
    public String MAX_LENGTH_MSG = "Your password must not exceed ${MAX_PASS} characters";
    public String PASS_EMPTY_MSG = "No password provided";
    public String USERNAME_EMPTY_MSG = "No username provided";
    public String INVALID_REQUEST_MSG = "Invalid Request";
    public String PASS_MIX_OF_CHARACTERS_MSG = "Your password must contain capital and non-capital letters, numbers and special characters.";
    public String PAST_PASSWORD_MSG = "This password has been used before, please try another one";
    public String VALID_MSG = "Valid Password";
    public String USERNAME_IN_PASSWORD = "Password contains username";
    public String PASSWORD_CONTAINS_USUAL_WORD = "Password contains either of the words ADMIN, PASSWORD or USER";
}
