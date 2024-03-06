package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;


public class LoginTest extends RapidAuthIntegrationTest {

	@Autowired
	TransactionTemplate transactionTemplate;

	@Test
	public void canLogin() throws Exception {
		String token = mvc.perform(userController.login(USER_CONTACT_INFORMATION, USER_PASSWORD))
				.andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").doesNotExist())
				.andExpect(jsonPath("$.password").doesNotExist())
				.andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);

		assertTokenWorks(token,getUser().getId());
	}

	@Test
	public void cantUseExpiredToken() throws Exception {
		// Test that a 50ms token does not expire before 50ms
		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD, 50L);
		// but, does expire after 50ms
		Thread.sleep(51L);
		assertTokenDoesNotWork(token);
	}

	/**
	 * Token won't work if the credentials of the user gets updated after token was issued
	 */
	@Test
	public void cantUseObsoleteToken() throws Exception {
		
		// credentials updated
		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		transactionTemplate.executeWithoutResult(new Consumer<>() {
			@SneakyThrows
			@Override
			public void accept(TransactionStatus transactionStatus) {
				AbstractUser<Serializable> user = getUserService().findById(getUser().getId()).get();
				user.setCredentialsUpdatedMillis(System.currentTimeMillis());
				getUserService().softUpdate(user);
			}
		});


		assertTokenDoesNotWork(token);
	}

	@Test
	public void cantLoginWithWrongPassword() throws Exception {
		mvc.perform(userController.login(ADMIN_CONTACT_INFORMATION,"wrong-password"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void cantLoginWithBlankPassword() throws Exception {
		mvc.perform(userController.login(ADMIN_CONTACT_INFORMATION,""))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void cantUseWrongToken() throws Exception {
		mvc.perform(get(authProperties.getController().getTestTokenUrl())
				.header(HttpHeaders.AUTHORIZATION, "Bearer a-wrong-token"))
				.andExpect(status().isUnauthorized());
	}
}
