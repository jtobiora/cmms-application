package ng.upperlink.nibss.cmms.model.authorization;

public enum AuthorizationStatus {
    NOT_FORWARDED("NOT FORWARDED"),
    UNKNOWN("UNKNOWN"), FORWARDED("FORWARDED"), REJECTED("REJECTED");
    String value;
    AuthorizationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static synchronized AuthorizationStatus find(String audit) {
        try {
            return AuthorizationStatus.valueOf(audit);
        } catch (Exception e) {
            return findByValue(audit);
        }
    }

    private static AuthorizationStatus findByValue(String value) {
        AuthorizationStatus type = null;

        for (AuthorizationStatus authorizationStatus : AuthorizationStatus.values()) {
            if( authorizationStatus.value.equalsIgnoreCase(value)) {
                type = authorizationStatus;
                break;
            }
        }
        return  type == null ? UNKNOWN: type;

    }
}
