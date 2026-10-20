package com.chaitanya.exception;

public class FileImportExcepiton extends Exception {

	private static final long serialVersionUID = 1L;
	
	public FileImportExcepiton(String message) {
		super(message);
	}

	public FileImportExcepiton(String message, Throwable cause) {
		super(message, cause);
	}
}
