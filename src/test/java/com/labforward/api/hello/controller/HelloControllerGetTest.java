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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.labforward.api.common.MVCIntegrationTest;
import com.labforward.api.core.validation.EntityValidator;
import com.labforward.api.hello.domain.Greeting;
import com.labforward.api.hello.service.HelloWorldService;

public class HelloControllerGetTest extends HelloControllerTest {

	@Test
	public void getHelloIsOKAndReturnsValidJSON() throws Exception {
		mockMvc.perform(get("/hello"))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.id", is(HelloWorldService.DEFAULT_ID)))
		       .andExpect(jsonPath("$.message", is(HelloWorldService.DEFAULT_MESSAGE)));
	}
}
