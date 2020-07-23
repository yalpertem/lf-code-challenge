package com.labforward.api.core.deletion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Marker class to return on successful
 * entity deleting
 */
public class EntityDeletedResponse extends ResponseEntity {
	
	public EntityDeletedResponse() {
		super(HttpStatus.OK);
	}
}
