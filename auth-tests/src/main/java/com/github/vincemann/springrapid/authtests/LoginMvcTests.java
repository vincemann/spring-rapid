package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoginMvcTests extends AbstractMvcTests {
	
	@Test
	public void testLogin() throws Exception {
		
		mvc.perform(post(authProperties.getController().getLoginUrl())
                .param("username", ADMIN_EMAIL)
                .param("password", ADMIN_PASSWORD)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
//				.andExpect(jsonPath("$.id").value(getAdmin().getId()))
				.andExpect(jsonPath("$.id").doesNotExist())
				.andExpect(jsonPath("$.password").doesNotExist());
		//get data via /context with token
//				.andExpect(jsonPath("$.email").value("admin@example.com"))
//				.andExpect(jsonPath("$.roles").value(hasSize(1)))
//				.andExpect(jsonPath("$.roles[0]").value(RapidRoles.ADMIN))
////				.andExpect(jsonPath("$.tag.name").value("Admin 1"))
//				.andExpect(jsonPath("$.unverified").value(false))
//				.andExpect(jsonPath("$.blocked").value(false))
//				.andExpect(jsonPath("$.admin").value(true))
//				.andExpect(jsonPath("$.goodUser").value(true));
//				.andExpect(jsonPath("$.goodAdmin").value(true));
	}

	@Test
	public void testLoginTokenExpiry() throws Exception {
		
//		// Test that default token does not expire before 10 days		
//		Thread.sleep(1001L);
//		mvc.perform(get("/api/core/ping")
//				.header(LemonSecurityConfig.TOKEN_REQUEST_HEADER_NAME, tokens.get(getAdmin().getId())))
//				.andExpect(status().is(204));
		
		// Test that a 500ms token does not expire before 500ms
		String token = successful_login(ADMIN_EMAIL, ADMIN_PASSWORD, 500L);
		// but, does expire after 500ms
		Thread.sleep(501L);
		mvc.perform(get("/api/core/ping")
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(401));
	}

	/**
	 * Token won't work if the credentials of the user gets updated afterwards
	 */
	@Test
	public void testObsoleteToken() throws Exception {
		
		// credentials updated
		// Thread.sleep(1001L);		
		AbstractUser<Long> user = getUserService().findById(getAdmin().getId()).get();
		user.setCredentialsUpdatedMillis(System.currentTimeMillis());
		getUserService().save(user);
		Thread.sleep(300);

		mvc.perform(get(authProperties.getController().getPingUrl())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
				.andExpect(status().is(401));
	}

	@Test
	public void testLoginWrongPassword() throws Exception {
		login(ADMIN_EMAIL,"wrong-password")
				.andExpect(status().is(401));
	}

	@Test
	public void testLoginBlankPassword() throws Exception {
		login(ADMIN_EMAIL,"")
				.andExpect(status().is(401));
	}

	@Test
	public void testGetUserIdByToken() throws Exception {

		mvc.perform(get(authProperties.getController().getContextUrl())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.user.id").value(getAdmin().getId()))
				.andReturn();
	}

	@Test
	public void testTokenLoginWrongToken() throws Exception {
		
		mvc.perform(get(authProperties.getController().getContextUrl())
				.header(HttpHeaders.AUTHORIZATION, "Bearer a-wrong-token"))
				.andExpect(status().is(401));
	}
	
	@Test
	public void testLogout() throws Exception {
		
		mvc.perform(post("/logout"))
                .andExpect(status().is(404));
	}
}
