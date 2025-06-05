package com.sunseed.exceptions;

public class SimulatedIdNullException extends RuntimeException {
    private int errorCode;

    public SimulatedIdNullException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

