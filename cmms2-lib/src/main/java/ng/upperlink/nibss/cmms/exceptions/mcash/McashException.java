package ng.upperlink.nibss.cmms.exceptions.mcash;

public class McashException extends RuntimeException {

    private int code;
    private String description;
    public McashException(String message, int code, String description)
    {
        super(message);
        this.code = code;
        this.description = description;
    }
}