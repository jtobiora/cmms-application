package ng.upperlink.nibss.cmms.exceptions.validator;

import ng.upperlink.nibss.cmms.dto.emandates.response.EmandateResponse;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EmandateValidatorExceptionMapper implements ExceptionMapper<EmandateValidatorException> {

    @Override
    public Response toResponse(EmandateValidatorException ex) {
        EmandateResponse emandateResponse = new EmandateResponse(ex.getMessage(), null,ex.getDescription());
        return Response.status(Response.Status.NOT_FOUND)
                .entity(emandateResponse)
                .build();
    }
}