package com.labforward.api.core.exception;

public class BadRequestException extends RuntimeException {

	public static String MESSAGE = "A required parameter is missing";

	public BadRequestException() {
		super(MESSAGE);
	}

	public BadRequestException(String message) {
		super(message);
	}
}
