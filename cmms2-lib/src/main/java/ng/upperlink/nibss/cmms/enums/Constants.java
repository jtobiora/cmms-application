package ng.upperlink.nibss.cmms.enums;

public interface Constants {

    String ID = "id";
    String CREATED_AT = "created_at";
    String UPDATED_AT = "updated_at";
    String CREATED_BY = "created_by";
    String MODIFIED_BY = "modified_by";
    String DESCRIPTION = "description";
    String START_DATE = "start_date";
    String END_DATE = "end_date";

    String PLACE_HOLDER = "{}";
    String EMPTY = "";

    String USER_DETAIL = "user_details";
    String BANK_DETAILS ="bank_details";
    String BILLER_DETAILS ="biller_details";
    String ACCESS_TOKEN ="access_token";
    String FILE_LOCATION = "C:\\BVN";
    String FILE_LOCATION_DECRYPTED = "C:\\BVN\\decrypted";
    String FILE_LOCATION_DECRYPTED_BMS = "C:\\BVN\\decrypted\\bms";
    String FILE_LOCATION_ENCRYPTED = "C:\\BVN\\encrypted";

    String ERROR_MESSAGE = "Unable to process request. Please try again.";
    String INVAlID_FILE_EXCEPTION = "Invalid file. Please check and try again.";
    String FILE_EXCEEDED_SIZE = "File exceeded maximum upload size";


    String AGENT_ID_IS_REQUIRED = "Agent Id is required";
    String BRANCH_ID_IS_REQUIRED = "Branch Id is required";

    String SECRET_KEY = "aeskey";
    String IV = "iv";
    String USERNAME = "username";

    String SCHEMA_NAME = "CmmsPortal";

    String UNKNOWN_MAKER_CHECKER_TYPE = "Invalid user authorization type. it is either OPERATOR or AUTHORIZER";


    Long BILLER_INITIATE_MANDATE=1L; // biller initiator to biller authorizer
    Long BILLER_AUTHORIZE_MANDATE=2L; // biller authorizer to bank initiator
    Long BILLER_REJECT_MANDATE=3L; //biller authorizer to biller initiator
    Long BILLER_APPROVE_MANDATE=4L; // biller authorizer to live
    Long BILLER_DISAPPROVE_MANDATE=5L; //biller authorizer declined


    Long BANK_AUTHORIZE_MANDATE=6L; //biller authorizer to bank initiator to bank authorizer
    Long BANK_REJECT_MANDATE=7L; // bank authorizer to bank initiator
    Long BANK_APPROVE_MANDATE=8L; // bank authorizer to live
    Long BANK_DISAPPROVE_MANDATE=9L; //bank authorizer declined
    Long BANK_INITIATE_MANDATE=10L; //bank initiator to bank authorizer
    Long BANK_BILLER_INITIATE_MANDATE=11L;
    Long BANK_BILLER_AUTHORIZE_MANDATE=12L;
    Long NIBSS_BILLER_INITIATE_MANDATE =13L;
    Long NIBSS_BILLER_AUTHORIZE_MANDATE = 14L;
    Long NIBSS_REJECT_MANDATE = 15L;
    Long PSSP_REJECT_MANDATE = 16L;
    Long PSSP_INITIATE_MANDATE = 17L;
    Long PSSP_AUTHORIZE_MANDATE = 18L;
    Long PSSP_APPROVE_MANDATE = 19L;

    int STATUS_ACTIVE=1;
    int STATUS_MANDATE_SUSPENDED=2;
    int STATUS_MANDATE_DELETED=3;

    int SERVICE_TYPE_POSTPAID=1;
    int SERVICE_TYPE_PREPAID=2;

    /*Report*/
    String REPORT_TYPE_MANDATE="mandateReport";
    String REPORT_TYPE="reportType";
    String REPORT_TYPE_DETAILED="detailedReportType";
    String REPORT_OBJECT_TYPE="reportObjectType";
    String REPORT_DATA="reportData";
    
    /**
     * Audit
     */
    String DEFAULT_SPRING_SECURITY_USERNAME = "anonymousUser";
    
    /**
     * Name Enquiry Operations
     * 
     */
    String DEBIT_ACTION = "DEBIT";
    String CREDIT_ACTION = "CREDIT";
    String NIBSS_BANK_CODE = "999";
    String NIBSS_DESTINATION_CODE = "999";
    String PAYMENT_REFERENCE_PREFIX = "CMMS/";

    /*Transaction Status*/
    String TRANSACTION_STATUS="transactionStatus";

    /*Channel*/
    int CHANNEL_PORTAL=1;
    int CHANNEL_API=2;

    /*Notification Types*/
    String BILLER_NOTIFICATION="1";
    String BANK_NOTIFICATION="2";
    String PSSP_NOTIFICATION="3";

    /*Biller Status*/
    int BILLER_STATUS_ACTIVE=1;
    int BILLER_STATUS_INACTIVE=0;

    /*Report Formats*/
    String EXCEL_REPORT_FORMAT="xls";
    String PDF_REPORT_FORMAT="pdf";
    String CSV_REPORT_FORMAT="csv";

    int PAYMENT_SUCCESSFUL = 2;
    int PAYMENT_IN_PROGRESS = 1;
    int PAYMENT_ENTERED = 0;
    int PAYMENT_REVERSED = 4;
    int PAYMENT_FAILED = 3;

    String TRANSACTION_TYPE_DEBIT = "D";
    String TRANSACTION_TYPE_CREDIT = "C";

    String OK_RESPONSE = "200";

    /** Mandate Request Response Codes **/
    String MANDATE_RESPONSE_SUCCESSFUL = "00";
    String MANDATE_RESPONSE_94 = "94"; /** TODO: Remember to change the name of this variable **/
    String MANDATE_RESPONSE_26 = "26";
    
    // Response Codes for external services
    String SUCCESSFUL = "00";
    String SERVICE_TIMEOUT = "97";
    String SYSTEM_MALFUNCTION = "96";

    //Bank Account Validation Constants
    int ACC_NUMBER_MAX_DIGITS = 10;
    int BVN_MAX_DIGITS = 11;
    
    // Transaction Request Summary Types and Messages
    String SUCCESSFUL_SUMMARY = "successful";
    String MONTH_TYPE_SUMMARY = "month";
    String YEAR_TYPE_SUMMARY = "year";
    String UNSUCCESSFUL_SUMMARY = "unsuccessful";
    String INVALID_SUMMARY_TYPE = "Invalid transaction summary type provided";
    String INVALID_DATE_SUMMARY_TYPE = "Invalid transaction summary date type provided";
    String INVALID_SUMMARY_DATE_RANGE = "Invalid Date Range Provided";
    String TRANSACTION_DETAILS_DATE_FORMAT = "yyyy-MM-dd";
    
    // Transaction Channels Representation
    String PORTAL_TRANSACTION = "Portal";
    String API_TRANSACTION = "API";
    
    //Kafka Topics
    String CREDIT_DEBIT_TOPIC = "mandateTransaction";
    String MANDATE_ADVICE_TOPIC = "mandateAdvice";
}
