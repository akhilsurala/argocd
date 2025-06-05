package com.sunseed.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgriGeneralParametersException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Object data;
	private HttpStatus httpStatus;

	public AgriGeneralParametersException() {

	}

	public AgriGeneralParametersException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public AgriGeneralParametersException(String message, HttpStatus httpStatus, Throwable cause) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public AgriGeneralParametersException(Object data, String message, HttpStatus httpStatus) {
		super(message);
		this.data = data;
		this.httpStatus = httpStatus;
	}

	public AgriGeneralParametersException(Object data, String message, HttpStatus httpStatus, Throwable cause) {
		super(message);
		this.data = data;
		this.httpStatus = httpStatus;
	}
}
