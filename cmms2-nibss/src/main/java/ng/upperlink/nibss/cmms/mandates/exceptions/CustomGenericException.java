package ng.upperlink.nibss.cmms.mandates.exceptions;

public class CustomGenericException extends RuntimeException {
    public CustomGenericException(String message) {
        super(message);
    }

    public CustomGenericException() {
    }

    public CustomGenericException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomGenericException(Throwable cause) {
        super(cause);
    }
}