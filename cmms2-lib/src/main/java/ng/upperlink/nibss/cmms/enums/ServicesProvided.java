package ng.upperlink.nibss.cmms.enums;


public enum ServicesProvided {

    CASH_IN("CASH_IN"),
    CASH_OUT("CASH_OUT"),
    ACCOUNT_OPENING("ACCOUNT_OPENING"),
    BILLS_PAYMENT("BILLS_PAYMENT"),
    AIRTIME_RECHARGE("AIRTIME_RECHARGE"),
    FUNDS_TRANSFER("FUNDS_TRANSFER"),
    BVN_ENROLMENT("BVN_ENROLMENT"),
    OTHERS("OTHERS"),
    ADDITIONAL_SERVICE_1("ADDITIONAL_SERVICE_1"),
    ADDITIONAL_SERVICE_2("ADDITIONAL_SERVICE_2"),

    //unknown should never be used. This is only returned if on
    //if the find method below does not return a value
    UNKNOWN("UNKNOWN");

    String value;

    ServicesProvided(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static synchronized ServicesProvided find(String serviceProvided) {
        try {
            return ServicesProvided.valueOf(serviceProvided);
        } catch (Exception e) {
            return findByValue(serviceProvided);
        }
    }

    public static ServicesProvided findByValue(String value) {
        ServicesProvided service = null;

        for (ServicesProvided servicesProvided : ServicesProvided.values()) {
            if( servicesProvided.value.equalsIgnoreCase(value)) {
                service = servicesProvided;
                break;
            }
        }

        return  service == null ? UNKNOWN : service;
    }
}
