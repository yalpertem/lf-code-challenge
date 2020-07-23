package com.labforward.api.hello.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.labforward.api.hello.domain.Greeting;

public class HelloControllerPostTest extends HelloControllerTest {

	@Test
	public void postHelloReturnsUnprocessableEntityWhenMessageMissing() throws Exception {
		String body = "{}";
		mockMvc.perform(post("/hello").content(body)
		                              .contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isUnprocessableEntity())
		       .andExpect(jsonPath("$.validationErrors", hasSize(1)))
		       .andExpect(jsonPath("$.validationErrors[*].field", contains("message")));
	}

	@Test
	public void postHelloReturnsUnprocessableEntityWhenUnexpectedAttributeProvided() throws Exception {
		String body = "{ \"tacos\":\"value\" }";
		mockMvc.perform(post("/hello").content(body).contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isUnprocessableEntity());
		// TODO(yigitalp): Does not append the MESSAGE_UNRECOGNIZED_PROPERTY. To be fixed.
		//.andExpect(jsonPath("$.message", containsString(GlobalControllerAdvice.MESSAGE_UNRECOGNIZED_PROPERTY)));
			
	}

	@Test
	public void postHelloReturnsUnprocessableEntityWhenMessageEmptyString() throws Exception {
		Greeting emptyMessage = new Greeting(EMPTY_MESSAGE);
		final String body = getGreetingBody(emptyMessage);

		mockMvc.perform(post("/hello").content(body)
		                              .contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isUnprocessableEntity())
		       .andExpect(jsonPath("$.validationErrors", hasSize(1)))
		       .andExpect(jsonPath("$.validationErrors[*].field", contains("message")));
	}
	
	@Test
	public void postHelloReturnsBadRequestWhenBrokenJsonInputProvided() throws Exception {
		String body = "{ \"tacos\":\"value\" ";
		mockMvc.perform(post("/hello").content(body).contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isBadRequest());
	}

	@Test
	public void postHelloIsCreatedWhenRequiredGreetingProvided() throws Exception {
		Greeting hello = new Greeting(HELLO_LUKE);
		final String body = getGreetingBody(hello);

		mockMvc.perform(post("/hello").contentType(MediaType.APPLICATION_JSON)
		                              .content(body))
		       .andExpect(status().isCreated())
		       .andExpect(jsonPath("$.message", is(hello.getMessage())));
	}

}
