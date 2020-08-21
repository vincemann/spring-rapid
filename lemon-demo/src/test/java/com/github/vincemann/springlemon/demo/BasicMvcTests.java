package com.github.vincemann.springlemon.demo;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.vincemann.springrapid.core.service.security.Role;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.github.vincemann.springlemon.auth.util.LecUtils;

public class BasicMvcTests extends AbstractMvcTests {
	
	@Test
	public void testPing() throws Exception {
		
		mvc.perform(get("/api/core/ping"))
				.andExpect(status().is(204));
	}
	
	@Test
	public void testGetContextLoggedIn() throws Exception {
		
		mvc.perform(get("/api/core/context")
				.header(HttpHeaders.AUTHORIZATION, tokens.get(ADMIN_ID)))
				.andExpect(status().is(200))
				.andExpect(header().string(LecUtils.TOKEN_RESPONSE_HEADER_NAME, containsString(".")))
				.andExpect(jsonPath("$.context.reCaptchaSiteKey").isString())
				.andExpect(jsonPath("$.user.id").value(ADMIN_ID))
				.andExpect(jsonPath("$.user.roles[0]").value(Role.ADMIN))
				.andExpect(jsonPath("$.user.password").doesNotExist());
	}
	
	@Test
	public void testGetContextWithoutLoggedIn_shouldOnlyReturnCaptcha() throws Exception {
		
		mvc.perform(get("/api/core/context"))
				.andExpect(status().is(200))
				.andExpect(header().doesNotExist(LecUtils.TOKEN_RESPONSE_HEADER_NAME))
				.andExpect(jsonPath("$.context.reCaptchaSiteKey").isString())
				.andExpect(jsonPath("$.user").doesNotExist());
	}	
}
