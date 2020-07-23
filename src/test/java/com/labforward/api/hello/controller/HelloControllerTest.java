package com.labforward.api.hello.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.labforward.api.common.MVCIntegrationTest;
import com.labforward.api.hello.domain.Greeting;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class HelloControllerTest extends MVCIntegrationTest {
	
	protected static final String EMPTY_MESSAGE = "";
	protected static final String HELLO_LUKE = "Hello Luke";
	protected static final String DUMMY_ID = "123";

	protected String getGreetingBody(Greeting greeting) throws JSONException {
		JSONObject json = new JSONObject().put("message", greeting.getMessage());

		if (greeting.getId() != null) {
			json.put("id", greeting.getId());
		}

		return json.toString();
	}
}
