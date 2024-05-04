package ng.upperlink.nibss.cmms.enums;


public enum SecurityConstants {

    SECRET("SecretKeyToGenJWTs"),
    EXPIRATION_TIME("172800000"), // 2 days
    TOKEN_PREFIX("Bearer"),
    HEADER_STRING("Authorization"),
    SIGN_UP_URL("/users/sign-up");

    private String value;

    SecurityConstants(String message) {
        this.value = message;
    }

    public String getValue() {
        return value;
    }
}
