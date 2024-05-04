package ng.upperlink.nibss.cmms.enums;


public enum ValidationStatus {

    ONGOING("10"),
    FAILED("97"),
    SUCCESS("00"),
    PENDING("11"),
    DUPLICATE("99"),
    BAD_VERSION("94"),
    BAD_SOURCE("93"),
    TIMEOUT("95"),
    CANCELLED("96"),
    UNKNOWN("98");

    ValidationStatus(String code){
        this.code = code;
    }
    private String code;
    public String getCode(){
        return code;
    }
    public ValidationStatus getByCode(String code){
        switch (code) {
            case "00": return ValidationStatus.SUCCESS;
            case "10": return ValidationStatus.ONGOING;
            case "11": return ValidationStatus.PENDING;
            case "93": return ValidationStatus.BAD_SOURCE;
            case "94": return ValidationStatus.BAD_VERSION;
            case "95": return ValidationStatus.TIMEOUT;
            case "96": return ValidationStatus.CANCELLED;
            case "97": return ValidationStatus.FAILED;
            case "98": return ValidationStatus.UNKNOWN;
            case "99": return ValidationStatus.DUPLICATE;
        }
        return ValidationStatus.UNKNOWN;
    }

}
