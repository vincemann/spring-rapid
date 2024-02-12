package com.github.vincemann.springrapid.authtests;

import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.sec.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;

import java.util.HashMap;
import java.util.Map;

import static com.github.vincemann.springrapid.core.util.ProxyUtils.aopUnproxy;


public class BasicAuthTest extends RapidAuthIntegrationTest {


	@SpyBean
	CoreProperties coreProperties;

	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		Map<String,Object> testSharedProperties = new HashMap<>();
		testSharedProperties.put("testKey","testValue");

		Mockito.when(aopUnproxy(coreProperties).getShared())
				.thenReturn(testSharedProperties);
	}



	@Test
	public void loggedInAdminCanGetFullContextInformation() throws Exception {

		String token = login2xx(ADMIN_CONTACT_INFORMATION, ADMIN_PASSWORD);
		mvc.perform(get(coreProperties.getContextUrl())
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.shared").value(hasEntry("testKey","testValue")))

				.andExpect(jsonPath("$.user.id").value(getAdmin().getId()))
				.andExpect(jsonPath("$.user.roles[0]").value(Roles.ADMIN))
				.andExpect(jsonPath("$.user.password").doesNotExist());
	}
	
	@Test
	public void anonCanGetOnlySharedContextInformation() throws Exception {

		mvc.perform(get(coreProperties.getContextUrl()))
				.andExpect(status().is(200))
				.andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION))
				.andExpect(jsonPath("$.shared").value(hasEntry("testKey","testValue")))
				.andExpect(jsonPath("$.user").doesNotExist());
	}
}
