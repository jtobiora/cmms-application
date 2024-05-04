package ng.upperlink.nibss.cmms.enums;

public enum RoleName {
          //NIBSS
    NIBSS_SUPER_ADMIN_INITIATOR("NIBSS SUPER ADMIN INITIATOR"),
    NIBSS_SUPER_ADMIN_AUTHORIZER("NIBSS SUPER ADMIN AUTHORIZER"),
    NIBSS_INITIATOR("NIBSS INITIATOR"),
    NIBSS_AUTHORIZER("NIBSS AUTHORIZER"),
    NIBSS_BILLER_INITIATOR("NIBSS BILLER INITIATOR"),
    NIBSS_BILLER_AUTHORIZER("NIBSS BILLER AUTHORIZER"),
    NIBSS_USER("NIBSS USER"),

         //BILLER
    BILLER_ADMIN_INITIATOR("BILLER ADMIN INITIATOR"),
    BILLER_ADMIN_AUTHORIZER("BILLER ADMIN AUTHORIZER"),
    BILLER_INITIATOR("BILLER INITIATOR"),
    BILLER_AUTHORIZER("BILLER AUTHORIZER"),
    BILLER_AUDITOR("BILLER AUDITOR"),

         //BANKS
    BANK_ADMIN_INITIATOR("BANK ADMIN INITIATOR"),
    BANK_ADMIN_AUTHORIZER("BANK ADMIN AUTHORIZER"),
    BANK_AUDITOR("BANK AUDITOR"),
    BANK_INITIATOR("BANK INITIATOR"),
    BANK_AUTHORIZER("BANK AUTHORIZER"),
    BANK_BILLER_INITIATOR("BANK BIILER INITIATOR"),
    BANK_BILLER_AUTHORIZER("BANK BIILER AUTHORIZER"),

       //PSSP
    PSSP_ADMIN_INITIATOR("PSSP ADMIN INITIATOR"),
    PSSP_ADMIN_AUTHORIZER("PSSP ADMIN AUTHORIZER"),
    PSSP_INITIATOR("PSSP INITIATOR"),
    PSSP_AUTHORIZER("PSSP AUTHORIZER"),
    PSSP_AUDITOR("PSSP AUDITOR"),


    //unknown should never be used. This is only returned if on
    //if the find method below does not return a value
    UNKNOWN("UNKNOWN");

    String value;

    RoleName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static synchronized RoleName find(String roleName) {
        try {
            return RoleName.valueOf(roleName);
        } catch (Exception e) {
            return findByValue(roleName);
        }
    }

    private static RoleName findByValue(String value) {
        RoleName type = null;

        for (RoleName roleName : RoleName.values()) {
            if( roleName.value.equalsIgnoreCase(value)) {
                type = roleName;
                break;
            }
        }
        return  type == null ? UNKNOWN : type;

    }


}
