package com.sunseed.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Object object = null;

	public ResourceNotFoundException(String message) {
		super(message);

	}

	public ResourceNotFoundException(Object object, String key) {
		super(key);
		this.object = object;
	}

	public ResourceNotFoundException() {

	}

}
