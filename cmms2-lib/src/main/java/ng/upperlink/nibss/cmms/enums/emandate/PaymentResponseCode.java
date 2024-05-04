package ng.upperlink.nibss.cmms.enums.emandate;

public enum PaymentResponseCode {

    PAYMENT_SUCCESSFUL("00","Payment was successful and subscriber has been debited "),
    PAYMENT_FAILED("01","Payment failed"),
    PAYMENT_ENTRED("02","Payment Entered"),
    PAYMENT_REVERSED("03","Payment Reversed"),
    PAYMENT_IN_PROGRESS("03","Payment in progress"),
    UNKNOWN("100","Unexpected error occured, Please try again!");

    String code;
    String value;

    PaymentResponseCode(String code, String value) {
        this.code = code;
        this.value = value;
    }
    private static PaymentResponseCode findByValue(String value) {
        PaymentResponseCode type = null;

        for (PaymentResponseCode roleName : PaymentResponseCode.values()) {
            if( roleName.value.equalsIgnoreCase(value)) {
                type = roleName;
                break;
            }
        }
        return  type == null ? UNKNOWN : type;

    }
    private PaymentResponseCode findByCode(String code) {
        PaymentResponseCode type = null;

        for (PaymentResponseCode paymentResponseCode : PaymentResponseCode.values()) {
            if( paymentResponseCode.code.equals(code)) {
                type = paymentResponseCode;
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
