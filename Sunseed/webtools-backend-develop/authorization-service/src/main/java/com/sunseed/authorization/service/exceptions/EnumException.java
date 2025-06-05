package com.sunseed.authorization.service.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnumException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Object data;
	private HttpStatus httpStatus;

	public EnumException() {

	}

	public EnumException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public EnumException(String message, HttpStatus httpStatus, Throwable cause) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public EnumException(Object data, String message, HttpStatus httpStatus) {
		super(message);
		this.data = data;
		this.httpStatus = httpStatus;
	}

	public EnumException(Object data, String message, HttpStatus httpStatus, Throwable cause) {
		super(message);
		this.data = data;
		this.httpStatus = httpStatus;
	}
}
