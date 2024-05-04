package ng.upperlink.nibss.cmms.exceptions.mcash;

import ng.upperlink.nibss.cmms.dto.emandates.response.EmandateResponse;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.exceptions.emandate.EmandateException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class McashExceptionMapper implements ExceptionMapper<EmandateException>{

    @Override
    public Response toResponse(EmandateException ex) {
        EmandateResponse emandateResponse = new EmandateResponse(ex.getMessage(),null,ex.getEmandateErrorCode());
        return Response.status(Response.Status.NOT_FOUND)
                .entity(emandateResponse)
                .build();
    }
}