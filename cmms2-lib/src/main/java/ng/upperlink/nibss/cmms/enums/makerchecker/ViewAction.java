package ng.upperlink.nibss.cmms.enums.makerchecker;

public enum ViewAction {
    UNAUTHORIZED("Unauthorized"),
    AUTHORIZED("Authorized "),
    REJECTED("Rejected") ;

    private String value;

    ViewAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return  value;
    }

}
