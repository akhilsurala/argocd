package com.sunseed.authorization.service.response;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {

	public static ResponseEntity<Object> responseHandler(Object loginResponseObject, String message,
			HttpStatus httpStatus) {

		Map<String, Object> loginResponse = new HashMap<>();
		loginResponse.put("data", loginResponseObject);
		loginResponse.put("message", message);
		loginResponse.put("httpStatus", httpStatus);
		return new ResponseEntity<>(loginResponse, httpStatus);
	}

	public static ResponseEntity<Object> responseWithEnumHandler(Object loginResponseObject, String enumName,
			String message, HttpStatus httpStatus) {

		Map<String, Object> loginResponse = new HashMap<>();
		loginResponse.put("data", loginResponseObject);
		loginResponse.put("message", enumName + message);
		loginResponse.put("httpStatus", httpStatus);
		return new ResponseEntity<>(loginResponse, httpStatus);
	}

	public static ResponseEntity<Object> responseHandlerForMethodArgumentNotValidException(Object loginResponseObject,
			Object message, HttpStatus httpStatus) {

		Map<String, Object> loginResponse = new HashMap<>();
		loginResponse.put("data", loginResponseObject);
		loginResponse.put("message", message);
		loginResponse.put("httpStatus", httpStatus);
		return new ResponseEntity<>(loginResponse, httpStatus);
	}
}
