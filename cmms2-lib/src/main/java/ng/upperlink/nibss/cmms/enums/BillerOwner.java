package ng.upperlink.nibss.cmms.enums;

public enum BillerOwner {
    NIBSS("NIBSS"),
    BANK("BANK"),
    PSSP("PSSP"),
    ORDINARY("ORDINARY");

    String value;

    BillerOwner(String value)
    {
        this.value = value;
    }
    public String getValue(){return value;}

    public static BillerOwner findByvalue( String value)
    {
        BillerOwner type = null;
        for (BillerOwner billerOwner:BillerOwner.values())
        {
            if (billerOwner.value.equalsIgnoreCase(value))
                type = billerOwner;
            break;
        }
        return type == null ? ORDINARY : type;
    }
}
