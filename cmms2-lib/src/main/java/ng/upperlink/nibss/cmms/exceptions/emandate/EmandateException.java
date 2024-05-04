package ng.upperlink.nibss.cmms.exceptions.emandate;

import lombok.Data;

import javax.ws.rs.core.Response;

@Data
public class EmandateException extends Exception {

    private Response.Status code;
    private String emandateErrorCode;
    public EmandateException(String message, Response.Status code, String emandateErrorCode)
    {
        super(message);
        this.code = code;
        this.emandateErrorCode = emandateErrorCode;
    }
}