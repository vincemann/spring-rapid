package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.coretest.util.TransactionalTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VerificationTest extends RapidAuthIntegrationTest {

	
	@Test
	public void givenUserSignedUp_whenFollowingLinkInMsg_thenUserGetsVerified() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		AuthMessage msg = userController.signup2xx(signupDto);
		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().is(204))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));

	}
	@Test
	public void cantVerifyTwiceWithSameCode() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		AuthMessage msg = userController.signup2xx(signupDto);
		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().is2xxSuccessful());

		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void cantVerifyWithInvalidCode() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		AuthMessage msg = userController.signup2xx(signupDto);

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

		SignupDto signupDto = createValidSignupDto();
		mockJwtExpirationTime(50L);
		AuthMessage msg = userController.signup2xx(signupDto);
		AbstractUser<Serializable> user = getUserService().findByContactInformation(signupDto.getContactInformation()).get();
		// expired token
		Thread.sleep(51L);
		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void givenUsersCredentialsUpdatedAfterSignup_whenTryingToUseNowObsoleteVerificationCode_thenForbidden() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		AuthMessage msg = userController.signup2xx(signupDto);

		TransactionalTestUtil.withinTransaction(transactionTemplate, () -> {
			AbstractUser<Serializable> savedUser = getUserService().findByContactInformation(signupDto.getContactInformation()).get();
			// Credentials updated after the verification token is issued
			savedUser.setCredentialsUpdatedMillis(System.currentTimeMillis());
			getUserService().fullUpdate(savedUser);
		});

		mvc.perform(userController.verifyUserWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}
}
