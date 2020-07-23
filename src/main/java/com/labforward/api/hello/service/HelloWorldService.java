package com.labforward.api.hello.service;

import com.labforward.api.core.exception.ResourceNotFoundException;
import com.labforward.api.core.validation.EntityValidator;
import com.labforward.api.hello.domain.Greeting;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class HelloWorldService {

	public static final String GREETING_NOT_FOUND = "Greeting Not Found";

	public static String DEFAULT_ID = "default";

	public static String DEFAULT_MESSAGE = "Hello World!";

	private Map<String, Greeting> greetings;

	private EntityValidator entityValidator;

	public HelloWorldService(EntityValidator entityValidator) {
		this.entityValidator = entityValidator;

		this.greetings = new HashMap<>(1);
		save(getDefault());
	}

	private static Greeting getDefault() {
		return new Greeting(DEFAULT_ID, DEFAULT_MESSAGE);
	}
	
	public void deleteGreeting(String id) {
		entityValidator.validateDelete(id);
		if (!this.greetings.containsKey(id)) {
			throw new ResourceNotFoundException();
		}
		this.greetings.remove(id);
	}
	
	public Greeting updateGreeting(String id, Greeting request) {
		entityValidator.validateUpdate(id, request);
		return save(request);
	}

	public Greeting createGreeting(Greeting request) {
		entityValidator.validateCreate(request);

		request.setId(UUID.randomUUID().toString());
		return save(request);
	}
	
	public List<Greeting> getGreetings() {
		Collection<Greeting> greetings = new ArrayList<Greeting>(this.greetings.values());
		return new ArrayList<Greeting>(greetings);
	}

	public Optional<Greeting> getGreeting(String id) {
		Greeting greeting = greetings.get(id);
		if (greeting == null) {
			return Optional.empty();
		}

		return Optional.of(greeting);
	}

	public Optional<Greeting> getDefaultGreeting() {
		return getGreeting(DEFAULT_ID);
	}

	private Greeting save(Greeting greeting) {
		this.greetings.put(greeting.getId(), greeting);

		return greeting;
	}
}
