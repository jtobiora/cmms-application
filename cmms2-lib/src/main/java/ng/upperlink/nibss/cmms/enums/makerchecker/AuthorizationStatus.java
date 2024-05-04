package ng.upperlink.nibss.cmms.enums.makerchecker;

public enum AuthorizationStatus {
    AUTHORIZED("Authorized"), 
    UNAUTHORIZED_TOGGLE("Toggle"),
    UNAUTHORIZED_CREATE("Create "),
    UNAUTHORIZED_UPDATE("Update"),
    CREATION_REJECTED("Creation Rejected"),
    UPDATE_REJECTED("Update Rejected"),
    TOGGLE_REJECTED("Toggle Rejected");

    private String value;

    AuthorizationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return  value;
    }

}
