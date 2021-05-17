package com.github.vincemann.springrapid.authtests;

import static com.github.vincemann.springrapid.auth.service.AbstractUserService.FORGOT_PASSWORD_SUBJECT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.vincemann.springrapid.auth.mail.MailData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class ForgotPasswordTest extends AbstractRapidAuthIntegrationTest {


	@Test
	public void anonCanIssueForgotPassword() throws Exception {
		MailData mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
		Assertions.assertEquals(FORGOT_PASSWORD_SUBJECT, mailData.getSubject());
		Assertions.assertEquals(USER_EMAIL,mailData.getTo());
	}


	@Test
	public void cantIssueForgotPasswordForInvalidEmail() throws Exception {

		// Unknown email
		testTemplate.forgotPassword(UNKNOWN_EMAIL)
				.andExpect(status().isNotFound());


		// Null email
		testTemplate.forgotPassword(null)
				.andExpect(status().isBadRequest());

		// Blank email
		testTemplate.forgotPassword("")
				.andExpect(status().isBadRequest());

		verify(unproxy(mailSender), never()).send(any());
	}
}
