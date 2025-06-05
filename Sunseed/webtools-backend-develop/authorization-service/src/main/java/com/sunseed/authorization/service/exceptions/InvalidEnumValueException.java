package com.sunseed.authorization.service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidEnumValueException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String enumName;
	private Object object =null;
	
	public InvalidEnumValueException() {
		
	}
	public InvalidEnumValueException(String key) {
		super(key);
	}
	public InvalidEnumValueException(Object object, String key) {
		super(key);
		this.object = object;
	}
	public InvalidEnumValueException(Object object,String enumName,String key) {
		super(key);
		this.object = object;
		this.enumName = enumName;
	}
	
}
