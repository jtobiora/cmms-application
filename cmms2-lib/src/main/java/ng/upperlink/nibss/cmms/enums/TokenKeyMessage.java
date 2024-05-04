package ng.upperlink.nibss.cmms.enums;

public enum TokenKeyMessage {
    JSON_MAPPING_EXCEPTION("800", "Could not process login request."),
    JSON_GENERATION_EXCEPTION("801", "Could not process login request."),
    IO_EXCEPTION("802", "Login failed."),
    INVALID_CLIENT_EXCEPTION("803", "Could not complete login request."),
    JWT_SIGNATURE_EXCEPTION("804", "Token not generated."),

    JWT_EXPIRED_SIGNATURE_EXCEPTION("805", "Could not complete login request!"),
    CLIENTKEY_RETRIEVAL_ERROR("806", "Unable to retrieve client key!"),
    INVALID_EMAIL("807", "Invalid email was detected!"),
    INVALID_REQUESTID("808", "Invalid request ID was detected"),
    INVALID_PHONE("809", "Invalid phone number detected!"),
    INVALID_USER_PASSED_IN_HEADER("810", "Invalid user was passed!"),
    NO_RECORD_FOUND("811", "No record was found!"),
    INVALID_TOKEN("812", "This token is invalid!"),
    USED_TOKEN("813", "This token has already been used!"),
    INVALID_REQUEST_TYPE("814", "Invalid Request type detected!"),
    EXPIRED_TOKEN("815", "Expired token!"),
    INVALID_SYSTEM_PARAM("816", "Parameters passed are not accurate!"),
    INVALID_FIRSTNAME("817", "Invalid name detected!"),
    INVALID_LASTNAME("818", "Invalid name detected!"),
    INVALID_BVN("819", "Invalid BVN detected!"),
    INVALID_CURRENCY("820", "Invalid currency detected!"),

    INVALID_OTP("821", "Invalid OTP passed!"),
    INVALID_OR_USED_OTP("822", "Invalid or already used OTP!"),
    INVALID_TRANSACTION_TYPE("823", "Invalid Transaction Type detected!"),
    EXCEPTION_ON_ACTIVITY_LOG("824", "Could not complete login request."),
    TOKEN_ERROR("825", "Error in generating token. Please try again. "),
    EXCEPTION_DB_ERROR("907", "Unknown error occured. Please try again."),
    EXCEPTION("908", "Unknown error occured. Please try again."),
    SYSTEM_ERROR("909", "Unknown error occured. Please try again."),
    FORMAT_ERROR("910", "Unknown error occured. Please try again."),
    SECURITY_VIOLATION("911", "Unknown error occured. Please try again."),
    SECURITY_VIOLATION_2("915", "Unknown error occured. Please try again."),
    DUPLICATE_REQUEST("914", "The request is a duplicate one."),
    DECRYPTION_ERROR("923", "Could not complete login request."),
    UNKNOWN("930", "Unknown error occured. Please try again.");

    private String code;
    private String value;

    TokenKeyMessage(String code, String value) {
        this.value = value;
        this.code = code;
    }

    TokenKeyMessage(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static TokenKeyMessage findById(String code) {
        TokenKeyMessage type = null;
        for (TokenKeyMessage tokenKeyMsg : TokenKeyMessage.values()) {
            if (tokenKeyMsg.code.equals(code)) {
                type = tokenKeyMsg;
                break;
            }
        }
        return type == null ? UNKNOWN : type;
    }

    public static TokenKeyMessage findByValue(String value) {
        TokenKeyMessage type = null;
        for (TokenKeyMessage tokenKeyMsg : TokenKeyMessage.values()) {
            if (tokenKeyMsg.value.equalsIgnoreCase(value)) {
                type = tokenKeyMsg;
                break;
            }
        }
        return type == null ? UNKNOWN : type;
    }

    public static synchronized TokenKeyMessage find(String name) {
        try {
            return TokenKeyMessage.valueOf(name);
        } catch (Exception ex) {
            return findByValue(name);
        }
    }
}
