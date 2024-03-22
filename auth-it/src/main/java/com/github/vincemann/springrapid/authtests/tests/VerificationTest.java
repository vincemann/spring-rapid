package com.github.vincemann.springrapid.authtests.tests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;

import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.USER_CONTACT_INFORMATION;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VerificationTest extends RapidAuthIntegrationTest {


	@Override
	protected void createTestUsers() throws Exception {
		super.createTestUsers();
	}

	@Test
	public void givenUserSignedUp_whenFollowingLinkInMsg_thenUserGetsVerified() throws Exception {
		AuthMessage msg = signupUser();
		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().is(204))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));

	}
	@Test
	public void cantVerifyTwiceWithSameCode() throws Exception {
		AuthMessage msg = signupUser();
		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().is2xxSuccessful());

		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void cantVerifyWithInvalidCode() throws Exception {
		AuthMessage msg = signupUser();

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
		AuthMessage msg = signupUser();
		AbstractUser user = (AbstractUser) userRepository.findByContactInformation(USER_CONTACT_INFORMATION).get();
		// expired token
		Thread.sleep(51L);
		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void givenUsersCredentialsUpdatedAfterSignup_whenTryingToUseNowObsoleteVerificationCode_thenForbidden() throws Exception {
		AuthMessage msg = signupUser();

		transactionTemplate.executeWithoutResult(transactionStatus -> {
			AbstractUser savedUser = (AbstractUser) userRepository.findByContactInformation(USER_CONTACT_INFORMATION).get();
			// Credentials updated after the verification token is issued
			savedUser.setCredentialsUpdatedMillis(System.currentTimeMillis());
		});

		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}


	protected AuthMessage signupUser(String contactInformation) throws Exception {
		getTestAdapter().signup(contactInformation);
		return userController.verifyMsgWasSent(contactInformation);
	}
}
