package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class VerificationTest extends RapidAuthIntegrationTest {

	@Autowired
	TransactionTemplate transactionTemplate;
	
	@Test
	public void canVerifyContactInformation() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		AuthMessage msg = userController.signup2xx(signupDto);
		mvc.perform(userController.verifyContactInformationWithLink(msg.getLink()))
				.andExpect(status().is(204))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));

	}


	@Test
	public void cantVerifyContactInformationTwiceWithSameCode() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		AuthMessage msg = userController.signup2xx(signupDto);
		mvc.perform(userController.verifyContactInformationWithLink(msg.getLink()))
				.andExpect(status().is2xxSuccessful());

		mvc.perform(userController.verifyContactInformationWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}

	// https://github.com/Gallopsled/pwntools/issues/1783
	@Test
	public void cantVerifyContactInformationWithInvalidData() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		AuthMessage msg = userController.signup2xx(signupDto);

		// null code
		mvc.perform(userController.verifyContactInformation(null))
				.andExpect(status().isBadRequest());

		// blank code
		mvc.perform(userController.verifyContactInformation(""))
				.andExpect(status().isUnauthorized());

		// Wrong audience
		String code = modifyCode(msg.getCode(),"wrong-audience",null,null,null,null);
		mvc.perform(userController.verifyContactInformation(code))
				.andExpect(status().isForbidden());
	}

	@Test
	public void cantVerifyContactInformationWithExpiredCode() throws Exception {

		SignupDto signupDto = createValidSignupDto();
		mockJwtExpirationTime(50L);
		AuthMessage msg = userController.signup2xx(signupDto);
		AbstractUser<Serializable> savedUser = getUserService().findByContactInformation(signupDto.getContactInformation()).get();
		// expired token
		Thread.sleep(51L);
		mvc.perform(userController.verifyContactInformationWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void usersCredentialsUpdatedAfterSignup_cantUseObsoleteVerificationCode() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		AuthMessage msg = userController.signup2xx(signupDto);

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@SneakyThrows
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				AbstractUser<Serializable> savedUser = getUserService().findByContactInformation(signupDto.getContactInformation()).get();

				// Credentials updated after the verification token is issued
				savedUser.setCredentialsUpdatedMillis(System.currentTimeMillis());
				getUserService().fullUpdate(savedUser);
			}
		});

		mvc.perform(userController.verifyContactInformationWithLink(msg.getLink()))
				.andExpect(status().isForbidden());
	}
}
