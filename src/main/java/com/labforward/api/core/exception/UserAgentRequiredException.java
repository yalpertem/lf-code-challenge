package com.labforward.api.core.exception;

public class UserAgentRequiredException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3168439970739605139L;
	public static String MESSAGE = "User-Agent header is required";

	public UserAgentRequiredException() {
		super(MESSAGE);
	}
}
