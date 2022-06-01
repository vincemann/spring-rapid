package com.github.vincemann.springrapid.authtests;

import static com.github.vincemann.springrapid.auth.service.AbstractUserService.FORGOT_PASSWORD_AUDIENCE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.vincemann.springrapid.auth.mail.MailData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;
import static com.github.vincemann.springrapid.core.util.ProxyUtils.aopUnproxy;
public class ForgotPasswordTest extends AbstractRapidAuthIntegrationTest {


	@Test
	public void anonCanIssueForgotPassword() throws Exception {
		MailData mailData = testTemplate.forgotPassword2xx(USER_CONTACT_INFORMATION);
		Assertions.assertEquals(FORGOT_PASSWORD_AUDIENCE, mailData.getTopic());
		Assertions.assertEquals(USER_CONTACT_INFORMATION,mailData.getTo());
	}


	@Test
	public void cantIssueForgotPasswordForInvalidContactInformation() throws Exception {

		// Unknown contactInformation
		mvc.perform(testTemplate.forgotPassword(UNKNOWN_CONTACT_INFORMATION))
				.andExpect(status().isNotFound());


		// Null contactInformation
		mvc.perform(testTemplate.forgotPassword(null))
				.andExpect(status().isBadRequest());

		// Blank contactInformation
		mvc.perform(testTemplate.forgotPassword(""))
				.andExpect(status().isBadRequest());

		verify(aopUnproxy(mailSender), never()).send(any());
	}
}
