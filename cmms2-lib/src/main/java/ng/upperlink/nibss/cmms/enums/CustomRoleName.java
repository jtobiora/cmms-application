package ng.upperlink.nibss.cmms.enums;

public enum CustomRoleName {
    //NIBSS
    NIBSS_SUPER_ADMIN_INITIATOR("nibss_super_admin_initiator@nibss.com"),
    NIBSS_SUPER_ADMIN_AUTHORIZER("nibss_super_admin_authorizer@nibss.com"),
    NIBSS_INITIATOR("nibss_admin_initiator@nibss.com"),
    NIBSS_AUTHORIZER("nibss_admin_authorizer@nibss.com"),
    NIBSS_BILLER_INITIATOR("nibss_biller_initiator@nibss.com"),
    NIBSS_BILLER_AUTHORIZER("nibss_biller_authorizer@nibss.com"),
    NIBSS_USER("nibss_user@nibss.com"),

    //BILLER
    BILLER_ADMIN_INITIATOR("biller_admin_initiator@biller.com"),
    BILLER_ADMIN_AUTHORIZER("biller_admin_authorizer@biller.com"),
    BILLER_INITIATOR("biller_initiator@biller.com"),
    BILLER_AUTHORIZER("biller_authorizer@biller.com"),
    BILLER_AUDITOR("biller_auditor@biller.com"),

    //BANKS
    BANK_ADMIN_INITIATOR("bank_admin_initiator@bank.com"),
    BANK_ADMIN_AUTHORIZER("bank_admin_authorizer@bank.com"),
    BANK_AUDITOR("bank_auditor@bank.com"),
    BANK_INITIATOR("bank_initiator@bank.com"),
    BANK_AUTHORIZER("bank_authorizer@bank.com"),
    BANK_BILLER_INITIATOR("bank_biller_initiator@bank.com"),
    BANK_BILLER_AUTHORIZER("bank_biller_authorizer@bank.com"),

    //PSSP
    PSSP_ADMIN_INITIATOR("pssp_admin_initiator@pssp.com"),
    PSSP_ADMIN_AUTHORIZER("pssp_admin_authorizer@pssp.com"),
    PSSP_INITIATOR("pssp_initiator@pssp.com"),
    PSSP_AUTHORIZER("pssp_authorizer@pssp.com"),
    PSSP_AUDITOR("pssp_auditor@pssp.com"),

    UNKNOWN("none");

    String value;

    CustomRoleName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static synchronized CustomRoleName find(String roleName) {
        try {
            return CustomRoleName.valueOf(roleName);
        } catch (Exception e) {
            return findByValue(roleName);
        }
    }

    private static CustomRoleName findByValue(String value) {
        CustomRoleName type = null;

        for (CustomRoleName roleName : CustomRoleName.values()) {
            if( roleName.value.equalsIgnoreCase(value)) {
                type = roleName;
                break;
            }
        }
        return  type == null ? UNKNOWN : type;

    }

}
