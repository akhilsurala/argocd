package com.sunseed.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConflictException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private Object object = null;

    public ConflictException() {

    }

    public ConflictException(String key) {
        super(key);
    }

    public ConflictException(Object object, String key) {
        super(key);
        this.object = object;
    }

}
