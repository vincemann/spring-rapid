package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.auth.domain.dto.SignupDto;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;

public class VerificationTest extends AbstractRapidAuthIntegrationTest {


	
	@Test
	public void canVerifyEmail() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		MailData mailData = testTemplate.signup2xx(signupDto);
		AbstractUser<Long> savedUser = getUserService().findByEmail(signupDto.getEmail()).get();
		testTemplate.verifyEmail(savedUser.getId(),mailData.getCode())
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
		AbstractUser<Long> savedUser = getUserService().findByEmail(signupDto.getEmail()).get();
		testTemplate.verifyEmail(savedUser.getId(),mailData.getCode())
				.andExpect(status().is2xxSuccessful());

		testTemplate.verifyEmail(savedUser.getId(),mailData.getCode())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cantVerifyEmailOfUnknownUser() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		MailData mailData = testTemplate.signup2xx(signupDto);
		testTemplate.verifyEmail(UNKNOWN_USER_ID,mailData.getCode())
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void cantVerifyEmailWithInvalidData() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		MailData mailData = testTemplate.signup2xx(signupDto);
		AbstractUser<Long> savedUser = getUserService().findByEmail(signupDto.getEmail()).get();
		
		// null code
		testTemplate.verifyEmail(savedUser.getId(),null)
				.andExpect(status().isBadRequest());

		// blank code
		testTemplate.verifyEmail(savedUser.getId(),"")
				.andExpect(status().isBadRequest());

		// Wrong audience
		String code = modCode(mailData.getCode(),"wrong-audience",null,null,null,null);
		testTemplate.verifyEmail(savedUser.getId(),code)
				.andExpect(status().isForbidden());
		
		// Wrong email
		code = modCode(mailData.getCode(),null,SECOND_USER_EMAIL,null,null,null);
		testTemplate.verifyEmail(savedUser.getId(),code)
				.andExpect(status().isForbidden());
	}

	@Test
	public void cantVerifyEmailWithExpiredCode() throws Exception {

		SignupDto signupDto = createValidSignupDto();
		mockJwtExpirationTime(50L);
		MailData mailData = testTemplate.signup2xx(signupDto);
		AbstractUser<Long> savedUser = getUserService().findByEmail(signupDto.getEmail()).get();
		// expired token
		Thread.sleep(51L);
		testTemplate.verifyEmail(savedUser.getId(),mailData.getCode())
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
		AbstractUser<Long> savedUser = getUserService().findByEmail(signupDto.getEmail()).get();

		// Credentials updated after the verification token is issued
		savedUser.setCredentialsUpdatedMillis(System.currentTimeMillis());
		getUserService().update(savedUser);

		testTemplate.verifyEmail(savedUser.getId(),mailData.getCode())
				.andExpect(status().isForbidden());
	}
}
