package ng.upperlink.nibss.cmms.enums.makerchecker;

public enum AuthorizationAction {

    APPROVE_CREATE("Authorize new {}"),
    APPROVE_UPDATE("Authorize update"),
    APPROVE_TOGGLE("Authorize toggle"),

    REJECT_CREATE("Reject create"),
    REJECT_TOGGLE("Reject Toggle"),
    REJECT_UPDATE("Reject Update");

    private String value;

    AuthorizationAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return  value;
    }

}
