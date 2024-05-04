package ng.upperlink.nibss.cmms.model.authorization;

public enum AuditStatus {
    AWAITING_AUTHORIZATION("AWAITING AUTHORIZATION"),
    AUTHORIZED("AUTHORIZED"),
    UNKNOWN("UNKNOWN"), ACTIVE("ACTIVE");
    String value;
    AuditStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static synchronized AuditStatus find(String audit) {
        try {
            return AuditStatus.valueOf(audit);
        } catch (Exception e) {
            return findByValue(audit);
        }
    }

    private static AuditStatus findByValue(String value) {
        AuditStatus type = null;

        for (AuditStatus approvalStatus : AuditStatus.values()) {
            if( approvalStatus.value.equalsIgnoreCase(value)) {
                type = approvalStatus;
                break;
            }
        }
        return  type == null ? UNKNOWN : type;

    }
}
