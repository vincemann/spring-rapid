package com.github.vincemann.springrapid.authtests.tests;

import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.vincemann.springrapid.auth.service.PasswordServiceImpl.FORGOT_PASSWORD_AUDIENCE;
import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.UNKNOWN_CONTACT_INFORMATION;
import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.USER_CONTACT_INFORMATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class ForgotPasswordTest extends AuthIntegrationTest {


	@Test
	public void anonCanIssueForgotPassword() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		userController.forgotPassword2xx(user.getContactInformation());
		AuthMessage msg = verifyMsgWasSent(user.getContactInformation());
		Assertions.assertEquals(FORGOT_PASSWORD_AUDIENCE, msg.getTopic());
		Assertions.assertEquals(USER_CONTACT_INFORMATION,msg.getRecipient());
	}


	@Test
	public void cantIssueForgotPasswordForInvalidContactInformation() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();

		// Unknown contactInformation
		mvc.perform(userController.forgotPassword(UNKNOWN_CONTACT_INFORMATION))
				.andExpect(status().isNotFound());


		// Null contactInformation
		mvc.perform(userController.forgotPassword(null))
				.andExpect(status().isBadRequest());

		// Blank contactInformation
		mvc.perform(userController.forgotPassword(""))
				.andExpect(status().isBadRequest());

		verifyNoMsgSent();
	}
}
