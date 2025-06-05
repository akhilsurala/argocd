package com.sunseed.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UnAuthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Object object = null;

	public UnAuthorizedException(String message) {
		super(message);

	}

	public UnAuthorizedException(Object object, String message) {
		super(message);
		this.object = object;
	}
}
