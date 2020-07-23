package com.labforward.api.hello.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.contains;

import org.junit.Test;

import com.labforward.api.hello.service.HelloWorldService;

public class HelloControllerGetTest extends HelloControllerTest {

	@Test
	public void getHelloIsOKAndReturnsValidJSON() throws Exception {
		mockMvc.perform(get("/hello"))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$[*].id", contains(HelloWorldService.DEFAULT_ID)))
		       .andExpect(jsonPath("$[*].message", contains(HelloWorldService.DEFAULT_MESSAGE)));
	}
	
	@Test
	public void getHelloIsOKWhenCalledWithDefaultIdReturnsValidJSON() throws Exception {
		mockMvc.perform(get("/hello/" + HelloWorldService.DEFAULT_ID))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.id", is(HelloWorldService.DEFAULT_ID)))
		       .andExpect(jsonPath("$.message", is(HelloWorldService.DEFAULT_MESSAGE)));
	}
}
