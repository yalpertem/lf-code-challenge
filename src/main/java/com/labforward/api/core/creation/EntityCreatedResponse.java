package com.labforward.api.core.creation;

import java.net.URI;

public class EntityCreatedResponse<T> {

	private final T entity;

	private final URI location;

	public EntityCreatedResponse(T entity, URI location) {
		this.entity = entity;
		this.location = location;
	}

	public T getEntity() {
		return entity;
	}

	public URI getLocation() {
		return location;
	}
}
