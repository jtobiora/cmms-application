package ng.upperlink.nibss.cmms.exceptions;

import lombok.Data;

@Data
public class CMMSException extends Exception {
    private String code;
    private String emandateErrorCode;
    public CMMSException(String message, String code, String emandateErrorCode)
    {
        super(message);
        this.code = code;
        this.emandateErrorCode = emandateErrorCode;
    }
}
