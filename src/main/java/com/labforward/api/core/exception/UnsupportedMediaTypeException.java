package com.labforward.api.core.exception;

/**
 * Exception to be thrown when unsupported Content-Type is requested
 * <p>
 * Note: no message supplied because application can not write
 * to the desired format
 */
public class UnsupportedMediaTypeException extends RuntimeException {

	public UnsupportedMediaTypeException() {
		super("");
	}
}
