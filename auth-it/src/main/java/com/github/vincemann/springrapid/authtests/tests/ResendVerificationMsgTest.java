package com.github.vincemann.springrapid.authtests.tests;

import static com.github.vincemann.springrapid.auth.service.VerificationServiceImpl.VERIFY_CONTACT_INFORMATION_AUDIENCE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;

public class ResendVerificationMsgTest extends AuthIntegrationTest {

	@Test
	public void userCanResendVerificationMailForOwnAccount() throws Exception {
		AbstractUser<?> user = testAdapter.createUnverifiedUser();
		String token = userController.login2xx(UNVERIFIED_USER_CONTACT_INFORMATION, UNVERIFIED_USER_PASSWORD);
		mvc.perform(userController.resendVerificationMsg(user.getContactInformation(),token))
				.andExpect(status().is2xxSuccessful());

		AuthMessage msg = userController.verifyMsgWasSent(UNVERIFIED_USER_CONTACT_INFORMATION);
		Assertions.assertEquals(UNVERIFIED_USER_CONTACT_INFORMATION,msg.getRecipient());
		Assertions.assertEquals(VERIFY_CONTACT_INFORMATION_AUDIENCE,msg.getTopic());
	}

	@Test
	public void userCantResendVerificationMailOfDiffUser() throws Exception {
		AbstractUser<?> unverifiedUser = testAdapter.createUnverifiedUser();
		AbstractUser<?> user = testAdapter.createUser();
		String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.resendVerificationMsg(unverifiedUser.getContactInformation(),token))
				.andExpect(status().isForbidden());
	}

	@Test
	public void adminCanResendVerificationMailOfDiffUser() throws Exception {
		AbstractUser<?> user = testAdapter.createUnverifiedUser();
		AbstractUser<?> admin = testAdapter.createAdmin();
		String token = userController.login2xx(ADMIN_CONTACT_INFORMATION, ADMIN_PASSWORD);
		userController.resendVerificationMsg2xx(user.getContactInformation(),token);
	}

	@Test
	public void anonCantResendVerificationMail() throws Exception {
		AbstractUser<?> user = testAdapter.createUnverifiedUser();
		mvc.perform(userController.resendVerificationMsg(user.getContactInformation(),""))
				.andExpect(status().isUnauthorized());

		verifyNoMsgSent();
	}
	
	@Test
	public void givenUserIsAlreadyVerified_whenTryingToResendVerificationMsg_thenBadRequest() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.resendVerificationMsg(user.getContactInformation(),token))
				.andExpect(status().isBadRequest());

		verifyNoMsgSent();
	}
	

	
	@Test
	public void cantResendVerificationMailOfNonExistingUser() throws Exception {
		AbstractUser<?> user = testAdapter.createUnverifiedUser();
		String token = userController.login2xx(UNVERIFIED_USER_CONTACT_INFORMATION, UNVERIFIED_USER_PASSWORD);
		mvc.perform(userController.resendVerificationMsg(UNKNOWN_USER_ID,token))
				.andExpect(status().isNotFound());
		
		verifyNoMsgSent();
	}
}
