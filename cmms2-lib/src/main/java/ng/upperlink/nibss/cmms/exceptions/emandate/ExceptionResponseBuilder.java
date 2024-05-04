package ng.upperlink.nibss.cmms.exceptions.emandate;


import ng.upperlink.nibss.cmms.dto.emandates.response.EmandateResponse;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;

import javax.ws.rs.core.Response;

public class ExceptionResponseBuilder {

    public static Response buildResponse(String errorMsg, int statusCode, String documentation)
    {
        EmandateResponse emandateResponse  = new EmandateResponse(errorMsg, null,documentation);
        return Response.status(statusCode).entity(emandateResponse).build();
    }
}
