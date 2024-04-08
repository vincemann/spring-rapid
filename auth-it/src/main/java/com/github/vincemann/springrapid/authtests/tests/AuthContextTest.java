package com.github.vincemann.springrapid.authtests.tests;

import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;

import com.github.vincemann.springrapid.auth.Roles;
import com.github.vincemann.springrapid.auth.util.AopProxyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.AopTestUtils;

import java.util.HashMap;
import java.util.Map;


public class AuthContextTest extends AuthIntegrationTest {


	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		Map<String,Object> testSharedProperties = new HashMap<>();
		testSharedProperties.put("testKey","testValue");

		AuthProperties propertiesSpy = AopTestUtils.getUltimateTargetObject(this.properties);
		Mockito.doReturn(testSharedProperties)
						.when(AopProxyUtils.unproxy(propertiesSpy)).getShared();
	}



	@Test
	public void adminCanGetFullContextInformation() throws Exception {
		AbstractUser<?> admin = testAdapter.createAdmin();
		String token = userController.login2xx(ADMIN_CONTACT_INFORMATION,ADMIN_PASSWORD);
		mvc.perform(get(contextUrl())
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.shared").value(hasEntry("testKey","testValue")))

				.andExpect(jsonPath("$.user.id").value(admin.getId()))
				.andExpect(jsonPath("$.user.roles").value(hasItem(Roles.ADMIN)))
				.andExpect(jsonPath("$.user.password").doesNotExist());
	}

	@Test
	public void authenticatedUserCanGetFullContextInformation() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		String token = userController.login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		mvc.perform(get(contextUrl())
						.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.shared").value(hasEntry("testKey","testValue")))

				.andExpect(jsonPath("$.user.id").value(user.getId()))
				.andExpect(jsonPath("$.user.roles").value(hasItem(Roles.USER)))
				.andExpect(jsonPath("$.user.password").doesNotExist());
	}
	
	@Test
	public void anonCanOnlyGetLimitedContextInformation() throws Exception {
		mvc.perform(get(contextUrl()))
				.andExpect(status().is(200))
				.andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION))
				.andExpect(jsonPath("$.shared").value(hasEntry("testKey","testValue")))
				.andExpect(jsonPath("$.user").doesNotExist());
	}

	protected String contextUrl(){
		return "/api/core/context";
	}
}
