package ng.upperlink.nibss.cmms.enums.makerchecker;

public enum EntityType {

    NIBSS_USER("NIBSS User"),
    BANK_USER("Bank User"),
    PSSP_USER("PSSP User"),
    BILLER_USER("Biller User"),
    SUBSCRIBER("Subscriber"),
    BANK("Bank"),
    EMANDATE("Emandate configuration"),
    PSSP("PSSP"),
    BILLER("Biller"),
    PRODUCT("Product"),
    INDUSTRY("Industry"),

    //unknown should never be used. This is only returned if on
    //if the find method below does not return a value
    UNKNOWN("UNKNOWN");

    String value;

    EntityType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static synchronized EntityType find(String userType) {
        try {
            return EntityType.valueOf(userType);
        } catch (Exception e) {
            return findByValue(userType);
        }
    }

    private static EntityType findByValue(String value) {
        EntityType type = null;

        for (EntityType userType : EntityType.values()) {
            if( userType.value.equalsIgnoreCase(value)) {
                type = userType;
                break;
            }
        }

        return  type == null ? UNKNOWN : type;

    }


}
