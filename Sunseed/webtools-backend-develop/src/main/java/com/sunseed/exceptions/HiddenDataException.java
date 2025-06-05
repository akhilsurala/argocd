package com.sunseed.exceptions;

public class HiddenDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public HiddenDataException(String message) {
        super(message);
    }
}
