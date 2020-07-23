package com.labforward.api.hello;

import com.labforward.api.core.exception.EntityValidationException;
import com.labforward.api.hello.domain.Greeting;
import com.labforward.api.hello.service.HelloWorldService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloWorldServiceTest {

	private static final String EMPTY_MESSAGE = "";
	private static final String EMPTY_ID = "";
	private static final String DUMMY_ID = "123";
	private static final String HELLO_LUKE = "Hello Luke";
	private static final String HELLO_JANE = "Hello Jane";

	@Autowired
	private HelloWorldService helloService;

	public HelloWorldServiceTest() {
	}

	@Test
	public void getDefaultGreetingIsOK() {
		Optional<Greeting> greeting = helloService.getDefaultGreeting();
		Assert.assertTrue(greeting.isPresent());
		Assert.assertEquals(HelloWorldService.DEFAULT_ID, greeting.get().getId());
		Assert.assertEquals(HelloWorldService.DEFAULT_MESSAGE, greeting.get().getMessage());
	}

	@Test(expected = EntityValidationException.class)
	public void createGreetingWithEmptyMessageThrowsException() {
		helloService.createGreeting(new Greeting(EMPTY_MESSAGE));
	}

	@Test(expected = EntityValidationException.class)
	public void createGreetingWithNullMessageThrowsException() {
		helloService.createGreeting(new Greeting(null));
	}

	@Test
	public void createGreetingOKWhenValidRequest() {
		Greeting request = new Greeting(HELLO_LUKE);

		Greeting created = helloService.createGreeting(request);
		Assert.assertEquals(HELLO_LUKE, created.getMessage());
	}
	
	@Test(expected = EntityValidationException.class)
	public void updateGreetingWithEmptyMessageThrowsException() {
		helloService.updateGreeting(DUMMY_ID, new Greeting(EMPTY_MESSAGE));
	}

	@Test(expected = EntityValidationException.class)
	public void updateGreetingWithNullMessageThrowsException() {
		helloService.updateGreeting(DUMMY_ID, new Greeting(null));
	}
	
	@Test(expected = EntityValidationException.class)
	public void updateGreetingWithEmptyIdThrowsException() {
		helloService.updateGreeting(EMPTY_ID, new Greeting(EMPTY_ID, HELLO_LUKE));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateGreetingWithNullIdThrowsException() {
		helloService.updateGreeting(null, new Greeting(null, HELLO_LUKE));
	}

	@Test
	public void updateGreetingOKWhenValidRequest() {
		Greeting request = new Greeting(HELLO_LUKE);
		Greeting created = helloService.createGreeting(request);
		created.setMessage(HELLO_JANE);
		Greeting updated = helloService.updateGreeting(created.getId(), created);
		
		Assert.assertEquals(HELLO_JANE, updated.getMessage());
	}
}
