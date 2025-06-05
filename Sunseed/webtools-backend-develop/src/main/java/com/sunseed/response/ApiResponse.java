package com.sunseed.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ApiResponse {

	@Autowired
	private MessageSource messageSource;

	public ResponseEntity<Object> ResponseHandler(boolean success, String message, HttpStatus httpStatus, Object data) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", success);
		response.put("message", messageSource.getMessage(message, null, LocaleContextHolder.getLocale()));
		response.put("httpStatus", httpStatus);
		response.put("data", data);
		return new ResponseEntity<>(response, httpStatus);
	}

	public ResponseEntity<Object> errorHandler(HttpStatus httpStatus, String message) {
		Map<String, Object> response = new HashMap<>();
		response.put("httpStatus", httpStatus);
		response.put("message", messageSource.getMessage(message, null, LocaleContextHolder.getLocale()));
		return new ResponseEntity<>(response, httpStatus);
	}

	public ResponseEntity<Object> loginResponseHandler(Object data, String message, HttpStatus httpStatus) {
		Map<String, Object> response = new HashMap<>();
		response.put("data", data);
		response.put("message", messageSource.getMessage(message, null, LocaleContextHolder.getLocale()));
		response.put("httpStatus", httpStatus);
		return new ResponseEntity<>(response, httpStatus);
	}

	public static ResponseEntity<Object> getUrlResponseHandler(String imageUrl, HttpStatus httpStatus) {
		Map<String, Object> data = new HashMap<>();
		data.put("url", imageUrl);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("data", data);
		responseData.put("httpStatus", httpStatus);

		return new ResponseEntity<>(responseData, httpStatus);
	}
	
	public ResponseEntity<Object> commonResponseHandler(Object data, String message, HttpStatus httpStatus) {

		Map<String, Object> response = new HashMap<>();
		response.put("data", data);
		response.put("message", messageSource.getMessage(message, null, LocaleContextHolder.getLocale()));
		response.put("httpStatus", httpStatus);
		return new ResponseEntity<>(response, httpStatus);
	}

	public ResponseEntity<Object> commonResponseHandlerWithoutMessageSource(Object data, String message,
			HttpStatus httpStatus) {

		Map<String, Object> response = new HashMap<>();
		response.put("data", data);
		response.put("message", message);
		response.put("httpStatus", httpStatus);
		return new ResponseEntity<>(response, httpStatus);
	}

	public ResponseEntity<Object> responseWithEnumHandler(Object data, String enumName, String message,
			HttpStatus httpStatus) {

		Map<String, Object> response = new HashMap<>();
		response.put("data", data);
		response.put("message",
				enumName + " " + messageSource.getMessage(message, null, LocaleContextHolder.getLocale()));
		response.put("httpStatus", httpStatus);
		return new ResponseEntity<>(response, httpStatus);
	}

	public ResponseEntity<Object> responseHandlerForMethodArgumentNotValidException(Object data,
			List<String> errorMessages, HttpStatus httpStatus) {
		
		String message = errorMessages.stream()
				.map(i -> messageSource.getMessage(i, null, LocaleContextHolder.getLocale()))
				.collect(Collectors.joining(" ; "));

		Map<String, Object> response = new HashMap<>();
		response.put("data", data);
		response.put("message", message);
		response.put("httpStatus", httpStatus);
		return new ResponseEntity<>(response, httpStatus);
	}
	public ResponseEntity<Object> webclientResponseHandler(Object data, String message, HttpStatus httpStatus) {


		Map<String, Object> response = new HashMap<>();
		response.put("data", data);
		response.put("message", message);
		response.put("httpStatus", httpStatus);
		return new ResponseEntity<>(response, httpStatus);
	}
}
