package ng.upperlink.nibss.cmms.enums.emandate;

public enum EmandateResponseCode {
    CREATION_SUCCESSFUL("00","Mandate creation was successful and approved"),
    ITEM_FOUND("01","Item was found"),
    INVALID_REQUEST("08","some of the inputs are invalid"),
    INVALID_START_DATE("09", "Mandate start date cannot be today {} or less!"),
    INVALID_FREQUENCY("10", "Specify a valid frequency for fixed mandate"),
    INVALID_DATE_RANGE("11","Mandate date range must be able to accommodate frequency!"),
    PRODUCT_NOT_FOUND("12","Could not find the product with id: {}"),
    PRODUCT_BILLER_NOT_FOUND("13","Biller attached to product with id: {} is not found"),
    SUBSCRIBER_BANK_NOT_FOUND("14","Subscriber's bank with code: {} has not been enrolled on CMMS"),
    EMPTY_MANDATE_REQUEST("15","Could not retrieve mandate request"),
    BILLER_NOT_FOUND("16","Biller authentication failed,provide a valid credentials"),
    BANK_NOT_FOUND("17","Bank authentication failed,provide a valid credentials"),
    CLIENT_NOT_FOUND("18","Client authentication failed,provide a valid credentials"),
    MANDATE_NOT_FOUND("19","Mandate with the reference code: {} is not found"),
    CHANNEL_NOT_FOUND("20","Channel with code: {} does not exist"),


    NOT_ACCEPTABLE("21","Bank approval failed, try again"),
    UNAUTHORIZED("22","Require access token to process this request"),
    EMANDAT_DISABLED("23","Electronic mandate is not enabled on {}. Please contact admin for reactivation"),
    PAYMENT_NOT_ALLOWED("24","This mandate was created as a fixed mandate. Instant debit is not permitted"),
    PARSE_DATE_ERROR("25","Invalid date format {}: must be yyyy-MM-dd"),
    INVALID_CREDENTIALS("26","Invalid username or password"),
    EMPTY_CREDENTIAL("27","Username or password or Api key is empty"),
    INVALID_API_KEY("28","Invalid Api key"),
    MANDATE_STATUS_REQUEST_FAILED("29","could not retrieve mandate status"),
    MCASH_SESSION_ID_GENERATION_FAILED("30","Could not generate the mcash sessionId"),
    MCASH_REQUEST_NOT_GENERATED("31","Could not generate mcash request"),
    MCASH_XML_REQUEST_NOT_GENERATED("32","Could not generate mcash request"),
    MCASH_AUTHENTICATION_FAILED("33","Error occurred during bank authorization"),
    MCASH_NO_RESPONSE("34","Could not get response from subscriber's bank {} "),
    MANDATE_NOT_GENERATEED("35","Mandate setup failed, please try again"),
    INVALID_ACCOUNT_NUMBER("36","Wrong account number"),
    UNTHORIZED_AMOUNT("37","Cannot debit more than initial configured amount {} "),
    UNTHORIZED_MANDATE("38","Subscriber's bank has not authorize debit on the subscriber's account "),
    NULL_POINTER("101","Null: invalid input. Please try again"),
    UNKNOWN("100","Unexpected error occurred, Please try again!");

    String code;
    String value;

    EmandateResponseCode(String code, String value) {
        this.code = code;
        this.value = value;
    }
    private static EmandateResponseCode findByValue(String value) {
        EmandateResponseCode type = null;

        for (EmandateResponseCode roleName : EmandateResponseCode.values()) {
            if( roleName.value.equalsIgnoreCase(value)) {
                type = roleName;
                break;
            }
        }
        return  type == null ? UNKNOWN : type;

    }
private EmandateResponseCode findByCode(String code) {
        EmandateResponseCode type = null;

        for (EmandateResponseCode emandateResponseCode : EmandateResponseCode.values()) {
            if( emandateResponseCode.code.equals(code)) {
                type = emandateResponseCode;
                break;
            }
        }
        return  type == null ? UNKNOWN : type;

    }
    public String getValue() {
        return  value;
    }
    public String getCode(){return code;}
}
