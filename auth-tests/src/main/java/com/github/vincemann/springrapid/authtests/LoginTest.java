package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.util.TransactionalTemplate;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;


public class LoginTest extends AbstractRapidAuthIntegrationTest {

	@Autowired
    TransactionalTemplate transactionalTemplate;

	@Test
	public void canLogin() throws Exception {
		String token = mvc.perform(testTemplate.login_builder(USER_CONTACT_INFORMATION, USER_PASSWORD))
				.andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").doesNotExist())
				.andExpect(jsonPath("$.password").doesNotExist())
				.andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);

		ensureTokenWorks(token,getUser().getId());
	}

	@Test
	public void cantUseExpiredToken() throws Exception {
		
//		// Test that default token does not expire before 10 days		
//		Thread.sleep(1001L);
//		mvc.perform(get("/api/core/ping")
//				.header(LemonSecurityConfig.TOKEN_REQUEST_HEADER_NAME, tokens.get(getAdmin().getId())))
//				.andExpect(status().is(204));
		
		// Test that a 50ms token does not expire before 50ms
		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD, 50L);
		// but, does expire after 50ms
		Thread.sleep(51L);
		assertTokenDoesNotWork(token);
//		mvc.perform(get(authProperties.getController().getPingUrl())
//				.header(HttpHeaders.AUTHORIZATION, token))
//				.andExpect(status().is(401));
	}

	/**
	 * Token won't work if the credentials of the user gets updated after token was issued
	 */
	@Test
	public void cantUseObsoleteToken() throws Exception {
		
		// credentials updated
		// Thread.sleep(1001L);
		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		transactionalTemplate.doInTransaction(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				AbstractUser<Long> user = getUserService().findById(getUser().getId()).get();
				user.setCredentialsUpdatedMillis(System.currentTimeMillis());
				getUserService().softUpdate(user);
			}
		});


		assertTokenDoesNotWork(token);
//		mvc.perform(get(authProperties.getController().getPingUrl())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
//				.andExpect(status().is(401));
	}

	@Test
	public void cantLoginWithWrongPassword() throws Exception {
		login(ADMIN_CONTACT_INFORMATION,"wrong-password")
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void cantLoginWithBlankPassword() throws Exception {
		login(ADMIN_CONTACT_INFORMATION,"")
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void cantUseWrongToken() throws Exception {
		mvc.perform(get(authProperties.getController().getContextUrl())
				.header(HttpHeaders.AUTHORIZATION, "Bearer a-wrong-token"))
				.andExpect(status().isUnauthorized());
	}
}
