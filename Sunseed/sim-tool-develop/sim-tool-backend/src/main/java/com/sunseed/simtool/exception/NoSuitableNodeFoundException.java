package com.sunseed.simtool.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoSuitableNodeFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	public NoSuitableNodeFoundException(String message) {
        super(message);
    }
}
