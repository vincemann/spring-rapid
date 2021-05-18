package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.user.RapidFindOwnUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;

public class LoginTest extends AbstractRapidAuthIntegrationTest {
	
	@Test
	public void canLogin() throws Exception {
		String token = testTemplate.login(USER_EMAIL, USER_PASSWORD)
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
		String token = login2xx(USER_EMAIL, USER_PASSWORD, 50L);
		// but, does expire after 50ms
		Thread.sleep(51L);
		ensureTokenDoesNotWork(token);
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
		String token = login2xx(USER_EMAIL, USER_PASSWORD);

		AbstractUser<Long> user = getUserService().findById(getUser().getId()).get();
		user.setCredentialsUpdatedMillis(System.currentTimeMillis());
		getUserService().save(user);

		ensureTokenDoesNotWork(token);
//		mvc.perform(get(authProperties.getController().getPingUrl())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
//				.andExpect(status().is(401));
	}

	@Test
	public void cantLoginWithWrongPassword() throws Exception {
		login(ADMIN_EMAIL,"wrong-password")
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void cantLoginWithBlankPassword() throws Exception {
		login(ADMIN_EMAIL,"")
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void cantUseWrongToken() throws Exception {
		mvc.perform(get(authProperties.getController().getContextUrl())
				.header(HttpHeaders.AUTHORIZATION, "Bearer a-wrong-token"))
				.andExpect(status().isUnauthorized());
	}
}
