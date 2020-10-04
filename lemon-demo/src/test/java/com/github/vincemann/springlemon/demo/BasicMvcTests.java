package com.github.vincemann.springlemon.demo;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.vincemann.springrapid.core.security.RapidRoles;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.github.vincemann.springlemon.auth.util.LemonValidationUtils;

public class BasicMvcTests extends AbstractMvcTests {
	
	@Test
	public void testPing() throws Exception {
		
		mvc.perform(get("/api/core/ping"))
				.andExpect(status().is(204));
	}
	
	@Test
	public void testGetContextLoggedIn() throws Exception {
		
		mvc.perform(get("/api/core/context")
				.header(HttpHeaders.AUTHORIZATION, tokens.get(admin.getId())))
				.andExpect(status().is(200))
//				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.context.reCaptchaSiteKey").isString())
				.andExpect(jsonPath("$.user.id").value(admin.getId()))
				.andExpect(jsonPath("$.user.roles[0]").value(RapidRoles.ADMIN))
				.andExpect(jsonPath("$.user.password").doesNotExist());
	}
	
	@Test
	public void testGetContextWithoutLoggedIn_shouldOnlyReturnCaptcha() throws Exception {
		
		mvc.perform(get("/api/core/context"))
				.andExpect(status().is(200))
				.andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION))
				.andExpect(jsonPath("$.context.reCaptchaSiteKey").isString())
				.andExpect(jsonPath("$.user").doesNotExist());
	}	
}
