package com.sunseed.authorization.service.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sunseed.authorization.service.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {

		e.printStackTrace();
		List<String> errors = new ArrayList<>();

		// Iterate over field errors
		for (FieldError error : e.getBindingResult().getFieldErrors()) {
			errors.add(error.getDefaultMessage());
		}

		// Iterate over global errors
		for (ObjectError error : e.getBindingResult().getGlobalErrors()) {
			errors.add(error.getDefaultMessage());
		}

		String result = errors.stream().collect(Collectors.joining(" ; "));

		// Return the list of errors directly
		return ApiResponse.responseHandlerForMethodArgumentNotValidException(null, result, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(InvalidEnumValueException.class)
	public ResponseEntity<Object> invalidEnumValueExceptionHandler(InvalidEnumValueException e) {
		e.printStackTrace();
		String enumName = e.getEnumName();
		String message = e.getMessage();
		Object object = e.getObject();
		return ApiResponse.responseWithEnumHandler(object, enumName, message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> illegalArgumentExceptionHandler(IllegalArgumentException e) {
		e.printStackTrace();
		String message = e.getMessage();
		return ApiResponse.responseHandler(null, message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Object> authenticationExceptionHandler(AuthenticationException e) {

		e.printStackTrace();
		Object data = e.getData();
		String key = e.getMessage();
		HttpStatus httpStatus = e != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		return ApiResponse.responseHandler(data, key, httpStatus);
	}
	
	@ExceptionHandler(EnumException.class)
	public ResponseEntity<Object> enumExceptionHandler(EnumException e) {

		e.printStackTrace();
		Object data = e.getData();
		String key = e.getMessage();
		HttpStatus httpStatus = e != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		return ApiResponse.responseHandler(data, key, httpStatus);
	}
	
	@ExceptionHandler(UserException.class)
	public ResponseEntity<Object> userExceptionHandler(UserException e) {

		e.printStackTrace();
		Object data = e.getData();
		String key = e.getMessage();
		HttpStatus httpStatus = e != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		return ApiResponse.responseHandler(data, key, httpStatus);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> parentExceptionHandler(Exception e) {
		e.printStackTrace();
		String message = e.getMessage();
		return ApiResponse.responseHandler(null, message, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
