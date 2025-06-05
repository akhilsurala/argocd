package com.sunseed.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PvParametersException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Object data;
	private HttpStatus httpStatus;

	public PvParametersException() {

	}

	public PvParametersException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public PvParametersException(String message, HttpStatus httpStatus, Throwable cause) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public PvParametersException(Object data, String message, HttpStatus httpStatus) {
		super(message);
		this.data = data;
		this.httpStatus = httpStatus;
	}

	public PvParametersException(Object data, String message, HttpStatus httpStatus, Throwable cause) {
		super(message);
		this.data = data;
		this.httpStatus = httpStatus;
	}
}
