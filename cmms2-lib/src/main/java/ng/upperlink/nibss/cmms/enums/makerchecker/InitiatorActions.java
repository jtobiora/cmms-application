package ng.upperlink.nibss.cmms.enums.makerchecker;

public enum InitiatorActions {

    TOGGLE("Toggle"),
    CREATE("Create "),
    UPDATE("Update");

    private String value;

    InitiatorActions(String value) {
        this.value = value;
    }

    public String getValue() {
        return  value;
    }
}
