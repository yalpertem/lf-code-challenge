package com.labforward.api.core.domain;

/**
 * Base message response
 */
public class ApiMessage {

	protected String message;

	protected String[] error;

	public ApiMessage(String message) {
		this.message = message;
		this.error = new String[0];
	}

	public ApiMessage(String message, String[] error) {
		this.message = message;
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public String[] getError() {
		return error;
	}
}
