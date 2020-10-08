package com.github.vincemann.springlemon.auth.tests;

import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.vincemann.springrapid.core.security.RapidRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

public class BasicMvcTests extends AbstractMvcTests {

	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		Map<String,Object> testSharedProperties = new HashMap<>();
		testSharedProperties.put("testKey","testValue");

		Mockito.when(properties.getShared())
				.thenReturn(testSharedProperties);
	}

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
				.andExpect(jsonPath("$.reCaptchaSiteKey").isString())
				.andExpect(jsonPath("$.shared").value(hasEntry("testKey","testValue")))

				.andExpect(jsonPath("$.user.id").value(admin.getId()))
				.andExpect(jsonPath("$.user.roles[0]").value(RapidRoles.ADMIN))
				.andExpect(jsonPath("$.user.password").doesNotExist())
				.andExpect(jsonPath("$.user.unverified").value(false))
				.andExpect(jsonPath("$.user.blocked").value(false))
				.andExpect(jsonPath("$.user.admin").value(true))
				.andExpect(jsonPath("$.user.goodUser").value(true));
	}
	
	@Test
	public void testGetContextWithoutLoggedIn_shouldOnlyReturnPublicContext() throws Exception {

		mvc.perform(get("/api/core/context"))
				.andExpect(status().is(200))
				.andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION))
				.andExpect(jsonPath("$.reCaptchaSiteKey").isString())
				.andExpect(jsonPath("$.shared").value(hasEntry("testKey","testValue")))
				.andExpect(jsonPath("$.user").doesNotExist());
	}
}
