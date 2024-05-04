package ng.upperlink.nibss.cmms.enums;


public enum Errors {

    INVALID_DATE_FORMAT("End date cannot be greater than start date!"),
    INVALID_REQUEST("Invalid request provided."),
    NOT_APPLICABLE("This action cannot be applied to PENDING requests."),
    INVALID_AGENT_MANAGER_INSTITUTION_CODE("Invalid agent manager institution code."),
    UNKNOWN_USER("unknown user."),
    UNKNOWN_DATA("unknown {}."),
    UNKNOWN_JOBROLE_ID("unknown job roles id."),
    UNKNOWN_GRADE_LEVEL_ID("unknown grade level id."),
    EXPIRED_SESSION("Expired Session"),
    EXPIRED_TOKEN("Expired Token"),
    ID_IS_NULL("Id is NULL"),
    DATA_IS_NULL("{} is NULL"),
    LIST_ID_IS_NULL("Request contains an Id value : NULL"),
    JOBROLETITLE_ALREADY_EXIST("Job roles title '{}' already exist"),
    GRADELEVEL_ALREADY_EXIST("Grade '{grade}' or Grade name '{gradeName}' already exist"),
    PRIORITY_ALREADY_EXIST("Priority '{}' already exist"),
    DATA_NAME_ALREADY_EXIST("'{name}' already exist"),
    ALREADY_APPROVED_OR_DISAPPROVED("Already approved or disapproved"),
    PARENT_AND_CHILD_CANNOT_BE_THE_SAME("Parent {} and child {} cannot be the same."),
    NOT_PERMITTED_TO_CREATE("Failed: Not permitted to create, update or toggle  {}"),
    NOT_PERMITTED_TO_AUTHORIZE("Failed: Not permitted to authorize or reject {}"),
    NOT_PERMITTED_TO_UPDATE_PSW("Only {} can update E-mandate password "),
    NOT_PERMITTED_TO_AUTHORIZE_PSW("Only {} can authorize password update for E-mandate"),
    NOT_PERMITTED_TO_ACT("Failed: Not permitted to act on : {}"),
    NOT_PERMIT("Failed: Not permitted to act on {} "),
    NOT_PERMITTED("Permission not granted."),
    DATA_NOT_PROVIDED("{} is not provided"),
    REQUEST_TERMINATED("Request processing could not be completed."),
    NO_UPDATE_REQUEST("No pending update found"),
    NO_ACTIONS_PERMITTED("Can not initiate toggle before approval"),
    INVALID_DATA_PROVIDED("Invalid {} provided"),
    INVALID_EMAIL_PROVIDED("Invalid enail: {} provided"),
    LENGTH_TOO_SHORT("Characters not less than {}"),
    LENGTH_TOO_LONG("Characters not more than {}"),
    INVALID_DEPARTMENT_PROVIDED("Invalid department Id provided"),
    JOB_ROLE_AND_SUPERVISOR_IS_NULL("Job roles AND supervisor cannot be null"),
    UNAUTHORIZED_APPROVER("Unauthorized approver"),
    JOB_ROLE_AND_SUPERVISOR_IS_NOT_NULL("Please select either Job roles OR supervisor");


    private String value;

    Errors(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
