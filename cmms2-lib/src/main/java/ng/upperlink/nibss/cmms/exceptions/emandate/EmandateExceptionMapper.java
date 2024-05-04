package ng.upperlink.nibss.cmms.exceptions.emandate;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.emandates.response.EmandateResponse;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class EmandateExceptionMapper implements ExceptionMapper<EmandateException>{

    @Override
    public Response toResponse(EmandateException ex) {
        EmandateResponse emandateExceptionResponse = new EmandateResponse(
                                                                        ex.getEmandateErrorCode(),
                                                                        ex.getMessage(),
                                                                        EmandateResponseCode.MANDATE_NOT_GENERATEED.getValue());
        String jsonObject = null;
        try {
            jsonObject = JsonBuilder.generateJson(emandateExceptionResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            try {
                throw new EmandateException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR,EmandateResponseCode.UNKNOWN.getCode());
            } catch (EmandateException e1) {
                log.error(e1.toString());
            }
        }
        return Response.status(ex.getCode())
                .entity(jsonObject)
                .build();
    }
}
