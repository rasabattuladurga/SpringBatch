package com.chaitanya.exception;

public class JobLaunchException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public JobLaunchException(String message) {
		super(message);
	}

	public JobLaunchException(String message, Throwable cause) {
		super(message, cause);
	}
}
