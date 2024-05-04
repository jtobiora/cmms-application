package ng.upperlink.nibss.cmms.tokenauth;

import javax.json.Json;
import javax.json.JsonObject;

public class TokenVerification {

    private String token = "";
    private String requestId = "";
    private String tokenFor = "";
    private String requestType = "";

    public TokenVerification(String token, String requestId, String tokenFor, String requestType) {
        this.token = token;
        this.requestId = requestId;
        this.tokenFor = tokenFor;
        this.requestType = requestType;
    }

    public JsonObject toJsonObject() {
        return Json.createObjectBuilder().add("token", this.token).
                add("requestId", this.requestId).
                add("tokenFor", this.tokenFor).
                add("requestType", this.requestType).build();
    }

    public String getToken() {
        return token;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getTokenFor() {
        return tokenFor;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
