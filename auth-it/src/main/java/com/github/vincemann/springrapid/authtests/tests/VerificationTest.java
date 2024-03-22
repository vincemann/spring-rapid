package com.github.vincemann.springrapid.authtests.tests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VerificationTest extends AuthIntegrationTest {


	@Test
	public void givenUserSignedUp_whenFollowingLinkInMsg_thenUserGetsVerified() throws Exception {
		AbstractUser<?> signupUser = testAdapter.signupUser();
		AuthMessage msg = verifyMsgWasSent(signupUser.getContactInformation());

		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().is(204))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));

	}
	@Test
	public void cantVerifyTwiceWithSameCode() throws Exception {
		AbstractUser<?> signupUser = testAdapter.signupUser();
		AuthMessage msg = verifyMsgWasSent(signupUser.getContactInformation());

		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().is2xxSuccessful());

		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void cantVerifyWithInvalidCode() throws Exception {
		AbstractUser<?> signupUser = testAdapter.signupUser();
		AuthMessage msg = verifyMsgWasSent(signupUser.getContactInformation());

		// null code
		mvc.perform(userController.verifyUser(null))
				.andExpect(status().isBadRequest());

		// blank code
		mvc.perform(userController.verifyUser(""))
				.andExpect(status().isUnauthorized());

		// Wrong audience
		String code = modifyCode(msg.getCode(),"wrong-audience",null,null,null,null);
		mvc.perform(userController.verifyUser(code))
				.andExpect(status().isForbidden());
	}

	@Test
	public void cantVerifyWithExpiredCode() throws Exception {
		mockJwtExpirationTime(50L);
		AbstractUser<?> signupUser = testAdapter.signupUser();
		AuthMessage msg = verifyMsgWasSent(signupUser.getContactInformation());

		// expired token
		Thread.sleep(51L);
		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void givenUsersCredentialsUpdatedAfterSignup_whenTryingToUseNowObsoleteVerificationCode_thenForbidden() throws Exception {
		AbstractUser<?> user = testAdapter.signupUser();
		AuthMessage msg = verifyMsgWasSent(user.getContactInformation());

		transactionTemplate.executeWithoutResult(transactionStatus -> {
			AbstractUser<?> savedUser = testAdapter.fetchUser(user.getContactInformation());
			// Credentials updated after the verification token is issued
			savedUser.setCredentialsUpdatedMillis(System.currentTimeMillis());
		});

		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}

}
