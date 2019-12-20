package com.labforward.api.core.exception;

import org.springframework.validation.BindingResult;

public class EntityValidationException extends RuntimeException {

	public static String MESSAGE = "Bad Request";

	private BindingResult bindingResult;

	public EntityValidationException(BindingResult bindingResult) {
		super(MESSAGE);
		this.bindingResult = bindingResult;
	}

	public BindingResult getBindingResult() {
		return this.bindingResult;
	}
}
