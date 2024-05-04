package ng.upperlink.nibss.cmms.enums;


public enum Success {

    SUCCESS("Successful");

    private String value;

    Success(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
