package com.sunseed.simtool.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidRequestBodyArgumentException.class)
	public ResponseEntity<?> handleInvalidRequestBodyArgument(InvalidRequestBodyArgumentException ex) {
		log.error("InvalidRequestBodyArgument: ", ex);
		return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		log.error("MethodArgumentNotValidException: ", ex);
		Map<String,String> errorMap = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			errorMap.put(error.getField(),error.getDefaultMessage());
		});
		return new ResponseEntity<>(errorMap,
				HttpStatus.BAD_REQUEST);
	}
}
