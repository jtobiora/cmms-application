package ng.upperlink.nibss.cmms.errorHandler;

import ng.upperlink.nibss.cmms.enums.Constants;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.toString(), Constants.ERROR_MESSAGE);
    logger.error("Exception => ",ex);
    ex.printStackTrace();
    return new ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }
//
//  @ExceptionHandler(IllegalArgumentException.class)
//  public final ResponseEntity<Object> handleIllegalArgumentExceptions(Exception ex, WebRequest request) {
//    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), Constants.ERROR_MESSAGE);
//    logger.error("IllegalArgumentException => ",ex);
//    return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
//  }
//

  @ExceptionHandler(NullPointerException.class)
  public final ResponseEntity<Object> handleObjectNotFoundExceptions(Exception ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), Constants.INVAlID_FILE_EXCEPTION);
    logger.error("Object Not Found => ",ex);
    return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidFileException.class)
  public final ResponseEntity<Object> handleInvalidFileExceptions(Exception ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), Constants.INVAlID_FILE_EXCEPTION);
    logger.error("Invalid File Exceptions => ",ex);
    return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
  }
//
//  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//  public final ResponseEntity<Object> handleMethodArgumentTypeMismatch(Exception ex, WebRequest request) {
//    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), Constants.ERROR_MESSAGE);
//    logger.error("MethodArgumentTypeMismatchException => ",ex);
//    return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
//  }
//
//  @Override
//  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//    List<String> errors = new ArrayList<>();
//    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
////      errors.add(error.getField() + " => " + error.getDefaultMessage());
//      errors.add(error.getDefaultMessage());
//    }
//    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
////      errors.add(error.getObjectName() + " => " + error.getDefaultMessage());
//      errors.add(error.getDefaultMessage());
//    }
//
//    ErrorDetails errorDetails = new ErrorDetails(new Date(), "Validation Failed", errors);
//    return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
//  }


}