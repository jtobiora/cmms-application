package ng.upperlink.nibss.cmms.enums.emandate;

public enum McashResponseCode {


    APPROVED("00","Approved"),
    STATUS_UNKNOWN("01", "Status unknown"),
    INVALID_SENDER("03" ,"Invalid Sender"),
    DON_NOT_HONOR("05", "DON_NOT_HONOR"),
    DORMANT_ACCOUNT("06","DORMANT ACCOUNT"),
    INVALID_ACCOUNT("07","INVALID_ACCOUNT"),
    ACCOUNT_NAME_MISMATCH("08","Account Name Mismatch"),
    PROCESSING_IN_PROGRESS("09","Request processing in progress"),
    INVALID_TRANSACTION("12","Invalid transaction"),
    INVALID_AMOUNT("13","Invalid Amount"),
    INVALID_BATCH_NUMBER("14","Invalid Batch Number"),
    INVALID_SESSION("15","Invalid Session or Record ID"),
    UNKNOWN_BANK_CODE("16","Unknown Bank Code/Requestor ID"),
    INVALI_CHANNEL("17","Invalid Channel"),
    WRONG_METHOD_CALL("18","Wrong Method Call"),
    NO_ACTION_TAKEN("21","No authorizationStatus taken"),
    INVALID_MERCHANT("22" ,"Invalid Merchant"),
    UNABLE_LOCATE_RECORD("25","Unable to locate record"),
    DUPLICATE_RECORD("26","Duplicate record"),
    FORMAT_ERROR("30","Format error"),
    SUSPECTED_FRAUD("34","Suspected fraud"),
    CONTACT_SENDING_BANK("35","Contact sending bank"),
    NO_SUFFICIENT_FUNDS("51","No sufficient funds"),
    TRANSACTION_NOT_PERMITTED_TO_SENDER("57","Transaction not permitted to sender"),
    TRANSACTION_NOT_PERMITTED("58","Transaction not permitted on channel"),
    TRANSFER_LIMIT_EXCEEDED("61", "Transfer limit Exceeded"),
    SECURITY_VIOLATION("63","Security violation"),
    EXCEEDS_WITHDRAWAL_FREQUENCY("65","Exceeds withdrawal frequency"),
    RESPONSE_RECEIVED_TOO_LATE("68"," Response received too late"),
    UNSUCCESSFUL_ACCOUNT("69", "Unsuccessful Account/Amount block"),
    UNSUCCESSFUL_AMOUNT_UNBLOCK("70", "Unsuccessful Account/Amount unblock"),
    EMPTY_MANDATE_REFERENCE_NUMBER("71", "Empty Mandate Reference Number"),
    BENEFICIARY_BANK_NOT_AVAILABLE("91", "Beneficiary Bank not available"),
    ROUTING_ERROR("92", "Routing error"),
    DUPLICATE("94"," Duplicate transaction"),
    SYSTEM_MALFUNCTION("96", "System malfunction"),
    TIMEOUT_WAITING("97"," Timeout waiting for response from destination");
    String code;
    String value;

    McashResponseCode(String code , String value)
    {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
    public String getResponseCode(String code)
    {
        for (McashResponseCode mcashResponseCode: McashResponseCode.values())
        {
            if (mcashResponseCode.getCode() == code) return mcashResponseCode.getValue();
        }
        return null;
    }
    public static McashResponseCode getResponseByCode(String code)
    {
        for (McashResponseCode mcashResponseCode : McashResponseCode.values())
        {
            if (mcashResponseCode.getCode().equals(code))return mcashResponseCode;
        }
        return null;
    }
}
