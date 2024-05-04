package ng.upperlink.nibss.cmms.util.response;

import org.springframework.data.domain.Page;

public class GenericResponse extends SuperResponse {

    //Specific Response structures
    private static GenericResponse getDefaultResponse(Object result, String message){
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setData(result);
        genericResponse.setMessage(message);
        genericResponse.setStatus(true);
        return genericResponse;
    }

    public static GenericResponse getErrorResponse(String message){
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setMessage(message);
        genericResponse.setStatus(false);
        return genericResponse;
    }

    public static GenericResponse getSuccessResponse(String message){
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setMessage(message);
        genericResponse.setStatus(true);
        return genericResponse;
    }

    public static GenericResponse getPaginatedResponse(Page<?> result, String message){
        return getDefaultResponse(result, message);
    }

//    public static GenericResponse getPaginatedResponse(Page<?> result, HttpServletRequest request){
//        PaginateResponse paginateResponse = new PaginateResponse(result, request);
//        GenericResponse genericResponse = new GenericResponse();
//        genericResponse.setData(paginateResponse);
//        genericResponse.setMessage(Constants.SUCCESS_RESPONSE_MESSAGE);
//        genericResponse.setStatus(true);
//        genericResponse.setStatus_code(Constants.STATUS_CODE_200);
//        genericResponse.setErrors(null);
//        return genericResponse;
//    }

//    public static GenericResponse getValidationResponse(HashMap<String, List<Object>> errors){
//        GenericResponse genericResponse = new GenericResponse();
//        genericResponse.setData(null);
//        genericResponse.setMessage(Validation.getMessage(Constants.VALIDATION_FORM_MESSAGE));
//        genericResponse.setStatus(false);
//        genericResponse.setStatus_code(Constants.STATUS_CODE_1400);
//        genericResponse.setErrors(errors);
//        return genericResponse;
//    }
    //End Specific Response structures

    //Start Generic Response structures
    public static GenericResponse getCreateResponse(Object result, String message){
        return getDefaultResponse(result, message);
    }

    public static GenericResponse getUpdateResponse(Object result, String message){
        return getDefaultResponse(result, message);
    }

    public static GenericResponse getAllResponse(Object[] result, String message){
        return getDefaultResponse(result, message);
    }

    public static GenericResponse getResponse(Object result, String message){
        return getDefaultResponse(result, message);
    }

    public static GenericResponse getToggleResponse(boolean status, String message){
        return getDefaultResponse(status, message);
    }
    //End Generic Response structures

//    public static GenericResponse getDeleteResponse(){
//        GenericResponse genericResponse = new GenericResponse();
//        genericResponse.setData(true);
//        genericResponse.setMessage(Constants.SUCCESS_RESPONSE_MESSAGE);
//        genericResponse.setStatus(true);
//        genericResponse.setStatus_code(Constants.STATUS_CODE_200);
//        genericResponse.setErrors(null);
//        return genericResponse;
//    }
}
