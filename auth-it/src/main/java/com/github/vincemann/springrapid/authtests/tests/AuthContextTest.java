package com.github.vincemann.springrapid.authtests.tests;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.sec.Roles;
import com.github.vincemann.springrapid.core.util.AopProxyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.AopTestUtils;

import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;

import java.util.HashMap;
import java.util.Map;


public class AuthContextTest extends RapidAuthIntegrationTest {


	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		Map<String,Object> testSharedProperties = new HashMap<>();
		testSharedProperties.put("testKey","testValue");

		CoreProperties coreProperties = AopTestUtils.getUltimateTargetObject(this.coreProperties);
		Mockito.doReturn(testSharedProperties)
						.when(AopProxyUtils.getUltimateTargetObject(coreProperties)).getShared();
	}



	@Test
	public void adminCanGetFullContextInformation() throws Exception {

		String token = login2xx(ADMIN_CONTACT_INFORMATION, ADMIN_PASSWORD);
		mvc.perform(get(coreProperties.getContextUrl())
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.shared").value(hasEntry("testKey","testValue")))

				.andExpect(jsonPath("$.user.id").value(getAdmin().getId()))
				.andExpect(jsonPath("$.user.roles").value(hasItem(Roles.ADMIN)))
				.andExpect(jsonPath("$.user.password").doesNotExist());
	}

	@Test
	public void authenticatedUserCanGetFullContextInformation() throws Exception {

		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(get(coreProperties.getContextUrl())
						.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.shared").value(hasEntry("testKey","testValue")))

				.andExpect(jsonPath("$.user.id").value(getUser().getId()))
				.andExpect(jsonPath("$.user.roles").value(hasItem(Roles.USER)))
				.andExpect(jsonPath("$.user.password").doesNotExist());
	}
	
	@Test
	public void anonCanOnlyGetLimitedContextInformation() throws Exception {

		mvc.perform(get(coreProperties.getContextUrl()))
				.andExpect(status().is(200))
				.andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION))
				.andExpect(jsonPath("$.shared").value(hasEntry("testKey","testValue")))
				.andExpect(jsonPath("$.user").doesNotExist());
	}
}
