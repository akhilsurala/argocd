package com.sunseed.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Object data;
	private HttpStatus httpStatus;

	public MailException() {

	}

	public MailException(String message) {
		super(message);
	}

	public MailException(Object data, String message, HttpStatus httpStatus) {
		super(message);
		this.data = data;
		this.httpStatus = httpStatus;
	}
}
