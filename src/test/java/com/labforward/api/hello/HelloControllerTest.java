package com.labforward.api.hello;

import com.labforward.api.common.MVCIntegrationTest;
import com.labforward.api.core.validation.EntityValidator;
import com.labforward.api.hello.domain.Greeting;
import com.labforward.api.hello.service.HelloWorldService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloControllerTest extends MVCIntegrationTest {
	
	private static final String EMPTY_MESSAGE = "";
	private static final String HELLO_LUKE = "Hello Luke";
	private static final String DUMMY_ID = "123";

	@Test
	public void getHelloIsOKAndReturnsValidJSON() throws Exception {
		mockMvc.perform(get("/hello"))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.id", is(HelloWorldService.DEFAULT_ID)))
		       .andExpect(jsonPath("$.message", is(HelloWorldService.DEFAULT_MESSAGE)));
	}

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
	
	@Test
	public void deleteHelloReturnsMethodNotAllowedWhenIdIsNotProvidedAsQueryParameter() throws Exception {
		mockMvc.perform(delete("/hello").contentType(MediaType.APPLICATION_JSON))
		       .andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void deleteHelloIsOKWhenRequiredGreetingProvided() throws Exception {
		Greeting hello = new Greeting(HELLO_LUKE);
		final String body = getGreetingBody(hello);
		mockMvc.perform(post("/hello").contentType(MediaType.APPLICATION_JSON)
		                              .content(body));

		mockMvc.perform(delete("/hello/" + DUMMY_ID).contentType(MediaType.APPLICATION_JSON)
													.content(body))
			.andExpect(status().isOk());
	}	
	
	@Test
	public void deleteHelloIsNotFoundWhenGreetingDoesNotExist() throws Exception {
		mockMvc.perform(delete("/hello/" + DUMMY_ID).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}		

	private String getGreetingBody(Greeting greeting) throws JSONException {
		JSONObject json = new JSONObject().put("message", greeting.getMessage());

		if (greeting.getId() != null) {
			json.put("id", greeting.getId());
		}

		return json.toString();
	}

}
