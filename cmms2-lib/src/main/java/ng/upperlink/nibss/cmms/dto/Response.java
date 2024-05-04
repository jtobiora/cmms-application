package ng.upperlink.nibss.cmms.dto;


public enum Response {

    OK("00"),
    NOT_FOUND("01"),
    EXPIRED("02"),
    EMPTY_STRING_VALUE("03"),
    INVALID_SEARCH_TYPE("04"),
    INVALID_LOGIN_CREDENTIALS("05"),
    INVALID_STRING_VALUE("06"),
    INVALID_DATA_PROVIDED("07"),
    SERVER_ERROR("08");


    String code;

    Response(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
