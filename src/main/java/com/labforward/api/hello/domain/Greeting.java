package com.labforward.api.hello.domain;

import javax.validation.constraints.NotEmpty;

import com.labforward.api.core.validation.Entity;
import com.labforward.api.core.validation.EntityUpdateValidatorGroup;

/**
 * Simple greeting message for dev purposes
 */
public class Greeting implements Entity {

	@NotEmpty(groups = {EntityUpdateValidatorGroup.class})
	private String id;

	@NotEmpty
	private String message;

	public Greeting() {
		// needed for JSON deserialization
	}

	public Greeting(String id, String message) {
		this.message = message;
		this.id = id;
	}

	public Greeting(String message) {
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
