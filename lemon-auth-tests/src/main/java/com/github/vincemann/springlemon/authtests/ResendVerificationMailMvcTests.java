package com.github.vincemann.springlemon.authtests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class ResendVerificationMailMvcTests extends AbstractMvcTests {

	@Test
	public void testResendVerificationMail() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/resend-verification-mail", unverifiedUser.getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId())))
			.andExpect(status().is(204));
		
		verify(mailSender).send(any());
	}

	@Test
	public void testAdminResendVerificationMailOtherUser() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/resend-verification-mail", unverifiedUser.getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(admin.getId())))
			.andExpect(status().is(204));
	}

	@Test
	public void testBlockedAdminResendVerificationMailOtherUser_shouldFail() throws Exception {
		
//		mvc.perform(post("/api/core/users/{id}/resend-verification-mail", unverifiedUser.getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(UNVERIFIED_ADMIN_ID)))
//			.andExpect(status().is(403));
		
		mvc.perform(post("/api/core/users/{id}/resend-verification-mail", unverifiedUser.getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(blockedAdmin.getId())))
			.andExpect(status().is(403));
		
		verify(mailSender, never()).send(any());
	}

	@Test
	public void testResendVerificationMailUnauthenticated() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/resend-verification-mail", unverifiedUser.getId()))
			.andExpect(status().is(403));
		
		verify(mailSender, never()).send(any());
	}
	
	@Test
	public void testResendVerificationMailAlreadyVerified() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/resend-verification-mail", user.getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(user.getId())))
			.andExpect(status().is(422));
		
		verify(mailSender, never()).send(any());
	}
	
	@Test
	public void testResendVerificationMailOtherUser() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/resend-verification-mail", unverifiedUser.getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(user.getId())))
			.andExpect(status().is(403));
		
		verify(mailSender, never()).send(any());
	}
	
	@Test
	public void testResendVerificationMailNonExistingUser() throws Exception {
		
		mvc.perform(post("/api/core/users/99/resend-verification-mail")
				.header(HttpHeaders.AUTHORIZATION, tokens.get(admin.getId())))
			.andExpect(status().is(404));
		
		verify(mailSender, never()).send(any());
	}
}
