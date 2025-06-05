package com.sunseed.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidDataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object object = null;

	public InvalidDataException(String message) {
		super(message);

	}

	public InvalidDataException(Object object, String message) {
		super(message);
		this.object = object;
	}
}