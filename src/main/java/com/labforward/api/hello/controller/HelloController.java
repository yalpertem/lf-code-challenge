package com.labforward.api.hello.controller;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.labforward.api.core.creation.EntityCreatedResponse;
import com.labforward.api.core.deletion.EntityDeletedResponse;
import com.labforward.api.core.exception.ResourceNotFoundException;
import com.labforward.api.hello.domain.Greeting;
import com.labforward.api.hello.service.HelloWorldService;

@RestController
public class HelloController {

	public static final String GREETING_NOT_FOUND = "Greeting Not Found";

	private HelloWorldService helloWorldService;

	public HelloController(HelloWorldService helloWorldService) {
		this.helloWorldService = helloWorldService;
	}

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	@ResponseBody
	public List<Greeting> getGreetings() {
		return helloWorldService.getGreetings();
	}

	@RequestMapping(value = "/hello/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Greeting getGreeting(@PathVariable String id) {
		return helloWorldService.getGreeting(id)
		                        .orElseThrow(() -> new ResourceNotFoundException(GREETING_NOT_FOUND));
	}

	@RequestMapping(value = "/hello", method = RequestMethod.POST)
	public EntityCreatedResponse<Greeting> createGreeting(@RequestBody Greeting request,
			HttpServletRequest httpRequest) {
		Greeting created = helloWorldService.createGreeting(request);
		String requestUrl = httpRequest.getRequestURL().toString();		
		URI createdUri = URI.create(requestUrl + "/" + created.getId());
		return new EntityCreatedResponse<Greeting>(created, createdUri);
	}
	
	@RequestMapping(value = "/hello/{id}", method = RequestMethod.PUT)
	public Greeting updateGreeting(@PathVariable String id, @RequestBody Greeting request) {
		return helloWorldService.updateGreeting(id, request);
	}
	
	@RequestMapping(value = "/hello/{id}", method = RequestMethod.DELETE)
	public EntityDeletedResponse deleteGreeting(@PathVariable String id) {
		helloWorldService.deleteGreeting(id);
		return new EntityDeletedResponse();
	}
}
