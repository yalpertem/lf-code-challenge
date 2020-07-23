package com.labforward.api.hello.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labforward.api.common.MVCIntegrationTest;
import com.labforward.api.core.creation.EntityCreatedResponse;
import com.labforward.api.core.validation.EntityValidator;
import com.labforward.api.hello.domain.Greeting;
import com.labforward.api.hello.service.HelloWorldService;

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
