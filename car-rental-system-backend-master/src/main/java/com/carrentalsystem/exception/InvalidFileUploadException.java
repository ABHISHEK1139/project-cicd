package com.carrentalsystem.exception;

/**
 * Thrown when an uploaded file fails basic validation (e.g. missing or
 * extensionless filename). Handled as HTTP 400 by GlobalExceptionHandler.
 */
public class InvalidFileUploadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidFileUploadException(String message) {
		super(message);
	}

}
