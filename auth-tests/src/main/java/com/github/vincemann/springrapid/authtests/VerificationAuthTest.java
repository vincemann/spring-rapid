package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.AuthRoles;
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

public class VerificationAuthTest extends AbstractRapidAuthTest {
	
	private String verificationCode;
	
	@Autowired
	private JweTokenService jweTokenService;
	
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		verificationCode = jweTokenService.createToken(RapidJwt.create(AbstractUserService.VERIFY_AUDIENCE,
				Long.toString(getUnverifiedUser().getId()), 60000L,
				MapUtils.mapOf("email", UNVERIFIED_USER_EMAIL)));
	}
	
	@Test
	public void canVerifyEmail() throws Exception {
//		Thread.sleep(1L);
		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
				.param("id",getUnverifiedUser().getId().toString())
                .param("code", verificationCode)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(getUnverifiedUser().getId()))
				.andExpect(jsonPath("$.roles").value(hasSize(1)))
				.andExpect(jsonPath("$.roles").value(Matchers.hasItem(AuthRoles.USER)))
				.andExpect(jsonPath("$.unverified").value(false))
				.andExpect(jsonPath("$.goodUser").value(true));

	}

	@Test
	public void alreadyVerified_cantVerifyEmailAgain() throws Exception {
//		Thread.sleep(1L);
		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.param("code", verificationCode)
				.header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(getUnverifiedUser().getId()))
				.andExpect(jsonPath("$.roles").value(hasSize(1)))
				.andExpect(jsonPath("$.roles").value(Matchers.hasItem(AuthRoles.USER)))
				.andExpect(jsonPath("$.unverified").value(false))
				.andExpect(jsonPath("$.goodUser").value(true));

		// Already verified
		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.param("code", verificationCode)
				.header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().is(400));
	}
	
	@Test
	public void cantVerifyEmailOfUnknownUser() throws Exception {

		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
				.param("id",UNKNOWN_USER_ID)
                .param("code", verificationCode)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(404));
	}
	
	@Test
	public void cantVerifyEmailWithInvalidData() throws Exception {
		
		// null token
		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
				.param("id",getUnverifiedUser().getId().toString())
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(400));

		// blank token
		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
				.param("id",getUnverifiedUser().getId().toString())
                .param("code", "")
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(400));

		// Wrong audience
		String token = jweTokenService.createToken(
				RapidJwt.create("wrong-audience",
				Long.toString(getUnverifiedUser().getId()), 60000L,
				MapUtils.mapOf("email", UNVERIFIED_USER_EMAIL)));
		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
				.param("id",getUnverifiedUser().getId().toString())
                .param("code", token)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(403));
		
		// Wrong email
		token = jweTokenService.createToken(
				RapidJwt.create(AbstractUserService.VERIFY_AUDIENCE,
				Long.toString(getUnverifiedUser().getId()), 60000L,
				MapUtils.mapOf("email", "wrong.email@example.com")));
		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
				.param("id",getUnverifiedUser().getId().toString())
                .param("code", token)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(403));

		// expired token
		token = jweTokenService.createToken(
				RapidJwt.create(AbstractUserService.VERIFY_AUDIENCE,
				Long.toString(getUnverifiedUser().getId()), 1L,
				MapUtils.mapOf("email", UNVERIFIED_USER_EMAIL)));
		// Thread.sleep(1001L);
		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
				.param("id",getUnverifiedUser().getId().toString())
                .param("code", token)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(403));
	}
	
	@Test
	public void usersCredentialsUpdated_cantUseNowObsoleteVerificationCode() throws Exception {
		
		// Credentials updated after the verification token is issued
//		Thread.sleep(1L);
		AbstractUser<Long> user = getUserService().findById(getUnverifiedUser().getId()).get();
		user.setCredentialsUpdatedMillis(System.currentTimeMillis());
		getUserService().save(user);

		Thread.sleep(50);
		mvc.perform(post(authProperties.getController().getVerifyUserUrl())
				.param("id",getUnverifiedUser().getId().toString())
                .param("code", verificationCode)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(403));
	}
}
