package com.github.vincemann.springrapid.authtests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class ResendVerificationMailAuthTest extends AbstractRapidAuthIntegrationTest {

	@Test
	public void canResendVerificationMailForOwnAccount() throws Exception {

		mvc.perform(post(authProperties.getController().getResendVerificationEmailUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId())))
			.andExpect(status().is(204));
		
		verify(unproxy(mailSender)).send(any());
	}

	@Test
	public void adminCanResendVerificationMailOfDiffUser() throws Exception {

		mvc.perform(post(authProperties.getController().getResendVerificationEmailUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
			.andExpect(status().is(204));
	}

	@Test
	public void blockedAdminCantResendVerificationMailOfDiffUser() throws Exception {
		
//		mvc.perform(post("/api/core/users/{id}/resend-verification-mail", getUnverifiedUser().getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(UNVERIFIED_ADMIN_ID)))
//			.andExpect(status().is(403));

		mvc.perform(post(authProperties.getController().getResendVerificationEmailUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getBlockedAdmin().getId())))
			.andExpect(status().is(403));
		
		verify(unproxy(mailSender), never()).send(any());
	}

	@Test
	public void anonCantResendVerificationMail() throws Exception {

		mvc.perform(post(authProperties.getController().getResendVerificationEmailUrl())
				.param("id",getUnverifiedUser().getId().toString()))
				.andExpect(status().is(403));
		
		verify(unproxy(mailSender), never()).send(any());
	}
	
	@Test
	public void alreadyVerified_cantResendVerificationMail() throws Exception {

		mvc.perform(post(authProperties.getController().getResendVerificationEmailUrl())
				.param("id",getUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUser().getId())))
			.andExpect(status().is(400));
		
		verify(unproxy(mailSender), never()).send(any());
	}
	
	@Test
	public void userCantResendVerificationMailOfDiffUser() throws Exception {

		mvc.perform(post(authProperties.getController().getResendVerificationEmailUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUser().getId())))
			.andExpect(status().is(403));
		
		verify(unproxy(mailSender), never()).send(any());
	}
	
	@Test
	public void cantResendVerificationMailOfNonExistingUser() throws Exception {

		mvc.perform(post(authProperties.getController().getResendVerificationEmailUrl())
				.param("id",UNKNOWN_USER_ID)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
			.andExpect(status().is(404));
		
		verify(unproxy(mailSender), never()).send(any());
	}
}
