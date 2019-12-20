package com.labforward.api.core.exception;

public class UserAgentRequiredException extends RuntimeException {

	public static String MESSAGE = "User-Agent header is required";

	public UserAgentRequiredException() {
		super(MESSAGE);
	}
}
