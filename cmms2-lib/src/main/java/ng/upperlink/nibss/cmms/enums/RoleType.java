package ng.upperlink.nibss.cmms.enums;

public enum RoleType {
    ADMIN ("ADMIN"),
    USER("USER"),
    SUPER_ADMIN("Super Admin"),
    UNKNOWN("UNKNOWN");

    String value;

    RoleType (String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static RoleType findByValus(String value){
        RoleType type = null;
        for (RoleType roleType : RoleType.values()){
            if (roleType.value.equalsIgnoreCase(value)){
                type = roleType;
                break;
            }
        }
        return type ==null ?UNKNOWN:type;
    }
}
