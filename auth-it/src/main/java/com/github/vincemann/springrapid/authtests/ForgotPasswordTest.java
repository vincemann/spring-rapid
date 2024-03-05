package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.vincemann.springrapid.auth.service.PasswordServiceImpl.FORGOT_PASSWORD_AUDIENCE;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.UNKNOWN_CONTACT_INFORMATION;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.USER_CONTACT_INFORMATION;
import static com.github.vincemann.springrapid.core.util.ProxyUtils.aopUnproxy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class ForgotPasswordTest extends RapidAuthIntegrationTest {


	@Test
	public void anonCanIssueForgotPassword() throws Exception {
		AuthMessage msg = userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
		Assertions.assertEquals(FORGOT_PASSWORD_AUDIENCE, msg.getTopic());
		Assertions.assertEquals(USER_CONTACT_INFORMATION,msg.getRecipient());
	}


	@Test
	public void cantIssueForgotPasswordForInvalidContactInformation() throws Exception {

		// Unknown contactInformation
		mvc.perform(userController.forgotPassword(UNKNOWN_CONTACT_INFORMATION))
				.andExpect(status().isNotFound());


		// Null contactInformation
		mvc.perform(userController.forgotPassword(null))
				.andExpect(status().isBadRequest());

		// Blank contactInformation
		mvc.perform(userController.forgotPassword(""))
				.andExpect(status().isBadRequest());

		verify(aopUnproxy(msgSender), never()).send(any());
	}
}
