package com.github.vincemann.springrapid.authtests.tests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class LoginTest extends AuthIntegrationTest {


	@Test
	public void login() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		String token = mvc.perform(userController.login(USER_CONTACT_INFORMATION, USER_PASSWORD))
				.andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").doesNotExist())
				.andExpect(jsonPath("$.password").doesNotExist())
				.andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);

		assertTokenWorks(token,user.getId());
	}

	@Test
	public void cantUseExpiredToken() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		// Test that a 50ms token does not expire before 50ms
		mockJwtExpirationTime(50);
		String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		// but, does expire after 50ms
		Thread.sleep(51L);
		assertTokenDoesNotWork(token);
	}

	/**
	 * Token won't work if the credentials of the user gets updated after token was issued
	 */
	@Test
	public void cantUseObsoleteToken() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		
		// credentials updated
		String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		transactionTemplate.executeWithoutResult(transactionStatus -> {
			AbstractUser update = testAdapter.fetchUser(user.getContactInformation());
			update.setCredentialsUpdatedMillis(System.currentTimeMillis());
		});

		assertTokenDoesNotWork(token);
	}

	@Test
	public void cantLoginWithWrongPassword() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		mvc.perform(userController.login(USER_CONTACT_INFORMATION,WRONG_PASSWORD))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void cantLoginWithBlankPassword() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		mvc.perform(userController.login(USER_CONTACT_INFORMATION,""))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void cantUseWrongToken() throws Exception {
		mvc.perform(get(properties.getController().getTestTokenUrl())
				.header(HttpHeaders.AUTHORIZATION, "Bearer a-wrong-token"))
				.andExpect(status().isUnauthorized());
	}
}
