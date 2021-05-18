package com.github.vincemann.springrapid.authtests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;

public class ResendVerificationMailTest extends AbstractRapidAuthIntegrationTest {

	@Test
	public void canResendVerificationMailForOwnAccount() throws Exception {
		String token = login2xx(UNVERIFIED_USER_EMAIL, UNVERIFIED_USER_PASSWORD);
		testTemplate.resendVerificationEmail(getUnverifiedUser().getId(),token)
				.andExpect(status().is2xxSuccessful());

		MailData mailData = testTemplate.verifyMailWasSend();
		Assertions.assertEquals(UNVERIFIED_USER_EMAIL,mailData.getTo());
		Assertions.assertEquals(AbstractUserService.VERIFY_EMAIL_SUBJECT,mailData.getSubject());
	}

	@Test
	public void userCantResendVerificationMailOfDiffUser() throws Exception {
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		testTemplate.resendVerificationEmail(getUnverifiedUser().getId(),token)
				.andExpect(status().isForbidden());

		verify(unproxy(mailSender), never()).send(any());
	}

	@Test
	public void adminCanResendVerificationMailOfDiffUser() throws Exception {
		String token = login2xx(ADMIN_EMAIL, ADMIN_PASSWORD);
		testTemplate.resendVerificationEmail2xx(getUnverifiedUser().getId(),token);
	}

	@Test
	public void blockedAdminCantResendVerificationMailOfDiffUser() throws Exception {
		String token = login2xx(BLOCKED_ADMIN_EMAIL, BLOCKED_ADMIN_PASSWORD);
		testTemplate.resendVerificationEmail(getUnverifiedUser().getId(),token)
				.andExpect(status().isForbidden());

		verify(unproxy(mailSender), never()).send(any());
	}

	@Test
	public void anonCantResendVerificationMail() throws Exception {
		testTemplate.resendVerificationEmail(getUnverifiedUser().getId(),"")
				.andExpect(status().isUnauthorized());
		
		verify(unproxy(mailSender), never()).send(any());
	}
	
	@Test
	public void alreadyVerified_cantResendVerificationMail() throws Exception {
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		testTemplate.resendVerificationEmail(getUser().getId(),token)
				.andExpect(status().isBadRequest());

		verify(unproxy(mailSender), never()).send(any());
	}
	

	
	@Test
	public void cantResendVerificationMailOfNonExistingUser() throws Exception {
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		testTemplate.resendVerificationEmail(UNKNOWN_USER_ID,token)
				.andExpect(status().isNotFound());
		
		verify(unproxy(mailSender), never()).send(any());
	}
}
