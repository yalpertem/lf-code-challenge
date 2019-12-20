package com.labforward.api.core.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Validation error message for use with invalid requests
 */
public class ValidationErrorMessage extends ApiMessage {

	private List<ValidationError> validationErrors = new ArrayList<>();

	public ValidationErrorMessage(String message) {
		super(message);
	}

	public void addError(String field, String message) {
		ValidationError error = new ValidationError(field, message);
		validationErrors.add(error);
	}

	public List<ValidationError> getValidationErrors() {
		return this.validationErrors;
	}
}
