package ng.upperlink.nibss.cmms.controller.errors;


import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@Slf4j
@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(ValidationException.class)
	public @ResponseBody Object handleCustomException(ValidationException ve, HttpServletRequest request) {
		log.info("...caught validation exception...");
		
		return new ResponseEntity<ErrorDetails>(new ErrorDetails(ve.getMessage()), HttpStatus.BAD_REQUEST);
 
	}
	
	@ExceptionHandler(Exception.class)
	public @ResponseBody Object handleGeneralException(HttpServletRequest request, Exception e) throws Exception {
		log.error("Error thrown {} ",e);
		return new ResponseEntity<ErrorDetails>(new ErrorDetails(e.getMessage().toString()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> errors = new ArrayList<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getDefaultMessage());
		}

		ErrorDetails errorDetails = new ErrorDetails(new Date(), "Validation Failed", errors);
		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
	}


}
