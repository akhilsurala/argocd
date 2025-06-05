package com.sunseed.simtool.exception;

public class TaskTimeExceededException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public TaskTimeExceededException() {
		// TODO Auto-generated constructor stub
	}
	
	public TaskTimeExceededException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public TaskTimeExceededException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
