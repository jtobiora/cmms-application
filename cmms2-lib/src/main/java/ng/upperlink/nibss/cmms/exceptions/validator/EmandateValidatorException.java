package ng.upperlink.nibss.cmms.exceptions.validator;

import lombok.Data;

@Data
public class EmandateValidatorException extends RuntimeException {
    private int code;
    private String description;
    public EmandateValidatorException(String message, int code, String description)
    {
        super(message);
        this.code = code;
        this.description = description;
    }
}