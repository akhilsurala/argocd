package com.sunseed.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sunseed.response.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestControllerAdvice
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalExceptionHandler {

	@Autowired
	private ApiResponse apiResponse;

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> resourceNotFoundExceptionHandler(ResourceNotFoundException e) {
		e.printStackTrace();
		String key = e.getMessage();
		return apiResponse.errorHandler(HttpStatus.NOT_FOUND, key);
	}

	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<Object> handleInvalidDataException(InvalidDataException ex) {
		ex.printStackTrace();
		Object object = ex.getObject();
		String message = ex.getMessage();
		return apiResponse.loginResponseHandler(object, message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
		e.printStackTrace();
		List<String> errors = new ArrayList<>();
		for (FieldError error : e.getBindingResult().getFieldErrors()) {
			errors.add(error.getDefaultMessage());
		}
		for (ObjectError error : e.getBindingResult().getGlobalErrors()) {
			errors.add(error.getDefaultMessage());
		}
		return apiResponse.responseHandlerForMethodArgumentNotValidException(null, errors, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(UnprocessableException.class)
	public ResponseEntity<Object> handleInvalidDataException(UnprocessableException ex) {
		ex.printStackTrace();
		String message = ex.getMessage();
		return apiResponse.errorHandler(HttpStatus.UNPROCESSABLE_ENTITY, message);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
	    // Log the exception (this logs a static message or a custom message for debugging)
//	    logger.warn("Entity not found: {}", ex.getMessage()); // Log a warning with a custom message
	    
	    // Return a structured API response with static data for the client (no stack trace or internal details)
		Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("httpStatus", HttpStatus.NOT_FOUND.value());
        response.put("data", null);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidEnumValueException.class)
	public ResponseEntity<Object> invalidEnumValueExceptionHandler(InvalidEnumValueException e) {
		e.printStackTrace();
		String enumName = e.getEnumName();
		String message = e.getMessage();
		Object object = e.getObject();
		return apiResponse.responseWithEnumHandler(object, enumName, message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnAuthorizedException.class)
	public ResponseEntity<Object> handleUnAuthorizedException(UnAuthorizedException ex) {
		Object object = ex.getObject();
		String message = ex.getMessage();
		return apiResponse.commonResponseHandler(object, message, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Object> authenticationExceptionHandler(AuthenticationException e) {

		e.printStackTrace();
		Object data = e.getData();
		String key = e.getMessage();
		HttpStatus httpStatus = e != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		return apiResponse.commonResponseHandler(data, key, httpStatus);
	}

	@ExceptionHandler(MailException.class)
	public ResponseEntity<Object> mailExceptionHandler(MailException e) {

		e.printStackTrace();
		Object data = e.getData();
		String key = e.getMessage();
		HttpStatus httpStatus = e != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		return apiResponse.commonResponseHandler(data, key, httpStatus);
	}

	@ExceptionHandler(UserProfileException.class)
	public ResponseEntity<Object> userProfileExceptionHandler(UserProfileException e) {

		e.printStackTrace();
		Object data = e.getData();
		String key = e.getMessage();
		HttpStatus httpStatus = e != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		return apiResponse.commonResponseHandler(data, key, httpStatus);
	}

	@ExceptionHandler(ProjectsException.class)
	public ResponseEntity<Object> projectsExceptionHandler(ProjectsException e) {

		e.printStackTrace();
		Object data = e.getData();
		String key = e.getMessage();
		HttpStatus httpStatus = e != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		return apiResponse.commonResponseHandler(data, key, httpStatus);
	}

	@ExceptionHandler(PvParametersException.class)
	public ResponseEntity<Object> pvParametersExceptionHandler(PvParametersException e) {

		e.printStackTrace();
		Object data = e.getData();
		String key = e.getMessage();
		HttpStatus httpStatus = e != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		return apiResponse.commonResponseHandler(data, key, httpStatus);
	}

	@ExceptionHandler(AgriGeneralParametersException.class)
	public ResponseEntity<Object> agriGeneralParametersExceptionHandler(AgriGeneralParametersException e) {

		e.printStackTrace();
		Object data = e.getData();
		String key = e.getMessage();
		HttpStatus httpStatus = e != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		return apiResponse.commonResponseHandler(data, key, httpStatus);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<Object> conflictExceptionHandler(ConflictException e) {
		e.printStackTrace();
		String message = e.getMessage();
//        Object object = e.getObject();
		return apiResponse.errorHandler(HttpStatus.CONFLICT, message);
	}

	// webclient exception
	@ExceptionHandler(WebclientException.class)
	public ResponseEntity<Object> handleWebClientException(WebclientException ex) {
		Object object = ex.getObject();
		HttpStatus httpStatus = ex.getHttpStatus();
		String message = ex.getMessage();
		return apiResponse.webclientResponseHandler(object, message, httpStatus);
	}
	
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("httpStatus", HttpStatus.BAD_REQUEST.value());
        response.put("data", null);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Optionally handle other exceptions globally
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "An unexpected error occurred.");
        response.put("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("data", null);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("httpStatus", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("data", null);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("httpStatus", HttpStatus.CONFLICT.value());
        errorResponse.put("data", null);

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(HiddenDataException.class)
    public ResponseEntity<Map<String, Object>> handleHiddenDataException(HiddenDataException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY.value());
        errorResponse.put("data", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
