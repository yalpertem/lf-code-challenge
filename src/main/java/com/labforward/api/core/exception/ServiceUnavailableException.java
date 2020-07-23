package com.labforward.api.core.exception;

public class ServiceUnavailableException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9148311263535548068L;

	public ServiceUnavailableException(String msg) {
		super(msg);
	}
}
