package ng.upperlink.nibss.cmms.exceptions;

public class ApplicationException extends Exception implements IException {

    private static final long serialVersionUID = 2008020602L;

    private int			   errorCode;

    private boolean		   logged;

    public ApplicationException( int errorCode, String message ) {
        super( message );
        this.errorCode = errorCode;
    }

    public ApplicationException( int errorCode, String message, Throwable th ) {
        super( message, th );
        this.errorCode = errorCode;
    }

    public final int getErrorCode() {
        return errorCode;
    }

    public final boolean isLogged() {
        return logged;
    }

    public final void setLogged( boolean logged ) {
        this.logged = logged;
    }

    public String getMessage() {
        return "errorCode[" + getErrorCode() + "]:: " + super.getMessage();
    }

}

