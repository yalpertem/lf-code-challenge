package com.labforward.api.hello.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.labforward.api.core.validation.EntityValidator;
import com.labforward.api.hello.domain.Greeting;

public class HelloControllerPutTest extends HelloControllerTest {
	
	@Test
	public void putHelloReturnsUnprocessableEntityWhenIdMissing() throws Exception {
		String body = "{}";
		mockMvc.perform(put("/hello/" + DUMMY_ID).content(body)
		                              .contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isUnprocessableEntity())
		       .andExpect(jsonPath("$.validationErrors", hasSize(1)))
		       .andExpect(jsonPath("$.validationErrors[*].message", contains(EntityValidator.MESSAGE_NO_ID_MATCH)));
	}
	
	@Test
	public void putHelloReturnsUnprocessableEntityWhenMessageMissing() throws Exception {
		Greeting hello = new Greeting();
		hello.setId(DUMMY_ID);
		final String body = getGreetingBody(hello);

		mockMvc.perform(put("/hello/" + DUMMY_ID).content(body)
		                              .contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isUnprocessableEntity())
		       .andExpect(jsonPath("$.validationErrors", hasSize(1)))
		       .andExpect(jsonPath("$.validationErrors[*].field", contains("message")));
	}

	@Test
	public void putHelloReturnsUnprocessableEntityWhenUnexpectedAttributeProvided() throws Exception {
		String body = "{ \"tacos\":\"value\" }";
		mockMvc.perform(put("/hello/" + DUMMY_ID).content(body).contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isUnprocessableEntity())
		       .andExpect(jsonPath("$.validationErrors[*].message", contains(EntityValidator.MESSAGE_NO_ID_MATCH)));
	}
	
	@Test
	public void putHelloReturnsUnprocessableEntityWhenIdIsNotProvidedInRequest() throws Exception {
		Greeting hello = new Greeting(HELLO_LUKE);	
		
		final String body = getGreetingBody(hello);
		mockMvc.perform(put("/hello/" + DUMMY_ID).content(body).contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isUnprocessableEntity())
		       .andExpect(jsonPath("$.validationErrors[*].message", contains(EntityValidator.MESSAGE_NO_ID_MATCH)));
	}
	
	@Test
	public void putHelloReturnsMethodNotAllowedWhenIdIsNotProvidedAsQueryParameter() throws Exception {
		Greeting hello = new Greeting(DUMMY_ID, HELLO_LUKE);
		final String body = getGreetingBody(hello);
		mockMvc.perform(put("/hello").content(body).contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void putHelloReturnsUnprocessableEntityWhenMessageEmptyString() throws Exception {
		Greeting emptyMessage = new Greeting(DUMMY_ID, EMPTY_MESSAGE);
		final String body = getGreetingBody(emptyMessage);

		mockMvc.perform(put("/hello/" + DUMMY_ID).content(body).contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isUnprocessableEntity())
		       .andExpect(jsonPath("$.validationErrors", hasSize(1)))
		       .andExpect(jsonPath("$.validationErrors[*].field", contains("message")));
	}
	
	@Test
	public void putHelloReturnsBadRequestWhenBrokenJsonInputProvided() throws Exception {
		String body = "{ \"tacos\":\"value\" ";
		mockMvc.perform(put("/hello/" + DUMMY_ID).content(body).contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isBadRequest());
	}

	@Test
	public void putHelloIsOKWhenRequiredGreetingProvided() throws Exception {
		Greeting hello = new Greeting(DUMMY_ID, HELLO_LUKE);
		final String body = getGreetingBody(hello);

		mockMvc.perform(put("/hello/" + DUMMY_ID).contentType(MediaType.APPLICATION_JSON)
		                              .content(body))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.message", is(hello.getMessage())));
	}

}
