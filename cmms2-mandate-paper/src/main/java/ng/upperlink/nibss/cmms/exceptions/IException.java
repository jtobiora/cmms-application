package ng.upperlink.nibss.cmms.exceptions;

/**
 * It represents all the customized exceptions which provides the user message
 * id and their logging status.
 *
 * Here user message id is used to show the error message to user in a user
 * presentable form which could be localized using some other framework.
 *
 *
 * */

public interface IException {

    /**
     * It returns the error code.
     *
     * Error Code can be used to distinguish the type of error from same class of
     * error and it can also be used to get the user presentable message using
     * some localization mechanism.
     *
     * @return error code
     */
    int getErrorCode();

    /**
     * Returns true/false based on logging status
     *
     * @return true if already logged, false otherwise
     */
    boolean isLogged();

    /**
     * It set the status of logging.
     *
     * @param logged logging status
     */
    void setLogged( boolean logged );

}
