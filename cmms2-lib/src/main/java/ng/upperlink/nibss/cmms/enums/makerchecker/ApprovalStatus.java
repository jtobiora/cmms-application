package ng.upperlink.nibss.cmms.enums.makerchecker;


public enum ApprovalStatus {

    ALL("ALL"),
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    DISAPPROVED("DISAPPROVED");

    String value;

    ApprovalStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static synchronized ApprovalStatus find(String userType) {
        try {
            return ApprovalStatus.valueOf(userType);
        } catch (Exception e) {
            return findByValue(userType);
        }
    }

    private static ApprovalStatus findByValue(String value) {
        ApprovalStatus type = null;

        for (ApprovalStatus approvalStatus : ApprovalStatus.values()) {
            if( approvalStatus.value.equalsIgnoreCase(value)) {
                type = approvalStatus;
                break;
            }
        }
        return  type == null ? ALL : type;

    }

}
