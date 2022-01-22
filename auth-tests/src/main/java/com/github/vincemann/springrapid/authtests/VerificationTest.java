package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.coretest.controller.TransactionalTestTemplate;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class VerificationTest extends AbstractRapidAuthIntegrationTest {

	@Autowired
	TransactionalTestTemplate transactionalTestTemplate;
	
	@Test
	public void canVerifyEmail() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		MailData mailData = testTemplate.signup2xx(signupDto);
		AbstractUser<Long> savedUser = getUserService().findByEmail(signupDto.getEmail()).get();
		mvc.perform(testTemplate.verifyEmailWithLink(mailData.getLink()))
				.andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(savedUser.getId()))
				.andExpect(jsonPath("$.roles").value(hasSize(1)))
				.andExpect(jsonPath("$.roles").value(Matchers.hasItem(AuthRoles.USER)))
				.andExpect(jsonPath("$.unverified").value(false))
				.andExpect(jsonPath("$.goodUser").value(true));
	}


	@Test
	public void cantVerifyEmailTwiceWithSameCode() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		MailData mailData = testTemplate.signup2xx(signupDto);
		mvc.perform(testTemplate.verifyEmailWithLink(mailData.getLink()))
				.andExpect(status().is2xxSuccessful());

		mvc.perform(testTemplate.verifyEmailWithLink(mailData.getLink()))
				.andExpect(status().isForbidden());
	}

//	@Test
//	public void cantVerifyEmailOfUnknownUser() throws Exception {
//		SignupDto signupDto = createValidSignupDto();
//		MailData mailData = testTemplate.signup2xx(signupDto);
//		testTemplate.verifyEmail(UNKNOWN_USER_ID,mailData.getCode())
//				.andExpect(status().isNotFound());
//	}

	// https://github.com/Gallopsled/pwntools/issues/1783
	@Test
	public void cantVerifyEmailWithInvalidData() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		MailData mailData = testTemplate.signup2xx(signupDto);

		// null code
		mvc.perform(testTemplate.verifyEmail(null))
				.andExpect(status().isBadRequest());

		// blank code
		mvc.perform(testTemplate.verifyEmail(""))
				.andExpect(status().isBadRequest());

		// Wrong audience
		String code = modCode(mailData.getCode(),"wrong-audience",null,null,null,null);
		mvc.perform(testTemplate.verifyEmail(code))
				.andExpect(status().isForbidden());

		// test makes no sense
//		// Wrong email/ userid
//		code = modCode(mailData.getCode(),null,getUnverifiedUser().getId().toString(),null,null,null);
//		testTemplate.verifyEmail(code)
//				.andExpect(status().isForbidden());

//		// Wrong user id
//		code = modCode(mailData.getCode(),null,getSecondUser().getId().toString(),null,null,null);
//		testTemplate.verifyEmail(code)
//				.andExpect(status().isForbidden());
	}

	@Test
	public void cantVerifyEmailWithExpiredCode() throws Exception {

		SignupDto signupDto = createValidSignupDto();
		mockJwtExpirationTime(50L);
		MailData mailData = testTemplate.signup2xx(signupDto);
		AbstractUser<Long> savedUser = getUserService().findByEmail(signupDto.getEmail()).get();
		// expired token
		Thread.sleep(51L);
		mvc.perform(testTemplate.verifyEmailWithLink(mailData.getLink()))
				.andExpect(status().isForbidden());

//
//		token = jweTokenService.createToken(
//				RapidJwt.create(AbstractUserService.VERIFY_SUBJECT,
//						Long.toString(getUnverifiedUser().getId()), 1L,
//						MapUtils.mapOf("email", UNVERIFIED_USER_EMAIL)));
//		// Thread.sleep(1001L);
//		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
//				.param("id",getUnverifiedUser().getId().toString())
//				.param("code", token)
//				.header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
//				.andExpect(status().is(403));
	}

	@Test
	public void usersCredentialsUpdatedAfterSignup_cantUseObsoleteVerificationCode() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		MailData mailData = testTemplate.signup2xx(signupDto);
		transactionalTestTemplate.doInTransaction(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				AbstractUser<Long> savedUser = getUserService().findByEmail(signupDto.getEmail()).get();

				// Credentials updated after the verification token is issued
				savedUser.setCredentialsUpdatedMillis(System.currentTimeMillis());
				getUserService().fullUpdate(savedUser);
			}
		});


		mvc.perform(testTemplate.verifyEmailWithLink(mailData.getLink()))
				.andExpect(status().isForbidden());
	}
}
