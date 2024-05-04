package ng.upperlink.nibss.cmms.enums;

public enum UserType {

    SYSTEM("System User"),
    NIBSS("NIBSS"),
    BANK("BANK"),
    PSSP("PSSP"),
    BILLER("BILLER"),
    SUBSCRIBER("SUBSCRIBER"),

    //unknown should never be used. This is only returned if on
    //if the find method below does not return a value
    UNKNOWN("UNKNOWN");

    String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static synchronized UserType find(String userType) {
        try {
            return UserType.valueOf(userType);
        } catch (Exception e) {
            return findByValue(userType);
        }
    }

    private static UserType findByValue(String value) {
        UserType type = null;

        for (UserType userType : UserType.values()) {
            if( userType.value.equalsIgnoreCase(value)) {
                type = userType;
                break;
            }
        }

        return  type == null ? UNKNOWN : type;

    }


}
