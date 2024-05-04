package ng.upperlink.nibss.cmms.enums;


public enum MakerCheckerType {

    OPERATOR("OPERATOR"),
    AUTHORIZER("AUTHORIZER"),
    AUDITOR("AUDITOR"),

    //unknown should never be used. This is only returned if on
    //if the find method below does not return a value
    UNKNOWN("UNKNOWN");

    String value;

    MakerCheckerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static synchronized MakerCheckerType find(String userType) {
        try {
            return MakerCheckerType.valueOf(userType);
        } catch (Exception e) {
            return findByValue(userType);
        }
    }

    private static MakerCheckerType findByValue(String value) {
        MakerCheckerType type = null;

        for (MakerCheckerType userType : MakerCheckerType.values()) {
            if( userType.value.equalsIgnoreCase(value)) {
                type = userType;
                break;
            }
        }

        return  type == null ? UNKNOWN : type;
    }

}
