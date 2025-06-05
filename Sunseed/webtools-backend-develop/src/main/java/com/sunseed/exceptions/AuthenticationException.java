package com.sunseed.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Object data;
	private HttpStatus httpStatus;

	public AuthenticationException() {

	}

	public AuthenticationException(String message) {
		super(message);
	}
	
	public AuthenticationException(String message,Throwable cause) {
		super(message,cause);
	}

	public AuthenticationException(Object data, String message, HttpStatus httpStatus) {
		super(message);
		this.data = data;
		this.httpStatus = httpStatus;
	}
	
	public AuthenticationException(Object data, String message, HttpStatus httpStatus,Throwable cause) {
		super(message,cause);
		this.data = data;
		this.httpStatus = httpStatus;
	}

}
