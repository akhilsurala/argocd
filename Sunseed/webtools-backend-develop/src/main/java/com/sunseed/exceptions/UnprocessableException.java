package com.sunseed.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnprocessableException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Object object = null;

	public UnprocessableException(String message) {
		super(message);

	}

	public UnprocessableException(Object object, String message) {
		super(message);
		this.object = object;
	}
	
	public UnprocessableException(String message, Throwable cause) {
        super(message, cause);
    }

}
