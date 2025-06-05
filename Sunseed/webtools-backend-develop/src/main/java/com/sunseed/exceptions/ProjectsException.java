package com.sunseed.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectsException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private Object data;
	private HttpStatus httpStatus;

	public ProjectsException() {

	}

	public ProjectsException(String message) {
		super(message);
	}

	public ProjectsException(Object data, String message, HttpStatus httpStatus) {
		super(message);
		this.data = data;
		this.httpStatus = httpStatus;
	}
}
