package com.labforward.api.hello.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labforward.api.hello.domain.Greeting;

public class HelloControllerDeleteTest extends HelloControllerTest {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void deleteHelloReturnsMethodNotAllowedWhenIdIsNotProvidedAsQueryParameter() throws Exception {
		mockMvc.perform(delete("/hello").contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void deleteHelloIsOKWhenRequiredGreetingProvided() throws Exception {
		Greeting hello = new Greeting(HELLO_LUKE);
		final String body = getGreetingBody(hello);
		
		ResultActions resultActions = mockMvc.perform(post("/hello").contentType(MediaType.APPLICATION_JSON)
																	.content(body));
		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		Greeting response = objectMapper.readValue(contentAsString, Greeting.class);	
		
		mockMvc.perform(delete("/hello/" + response.getId()).contentType(MediaType.APPLICATION_JSON)
															.content(body))
		.andExpect(status().isOk());
	}	
	
	@Test
	public void deleteHelloIsNotFoundWhenGreetingDoesNotExist() throws Exception {
		mockMvc.perform(delete("/hello/" + DUMMY_ID).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
	
}
