package com.github.vincemann.springlemon.authtests;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonRoles;
import com.github.vincemann.springlemon.auth.service.AbstractUserService;
import com.github.vincemann.springlemon.auth.service.token.EmailJwtService;
import com.github.vincemann.springlemon.auth.util.LemonMapUtils;
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

public class VerificationMvcTests extends AbstractMvcTests {
	
	private String verificationCode;
	
	@Autowired
	private EmailJwtService emailJwtService;
	
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		verificationCode = emailJwtService.createToken(AbstractUserService.VERIFY_AUDIENCE,
				Long.toString(unverifiedUser.getId()), 60000L,
				LemonMapUtils.mapOf("email", UNVERIFIED_USER_EMAIL));
	}
	
	@Test
	public void testEmailVerification() throws Exception {
		Thread.sleep(1L);
		mvc.perform(post("/api/core/users/{userId}/verification", unverifiedUser.getId())
                .param("code", verificationCode)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(unverifiedUser.getId()))
				.andExpect(jsonPath("$.roles").value(hasSize(1)))
				.andExpect(jsonPath("$.roles").value(Matchers.hasItem(LemonRoles.USER)))
				.andExpect(jsonPath("$.unverified").value(false))
				.andExpect(jsonPath("$.goodUser").value(true));
		
		// Already verified
		mvc.perform(post("/api/core/users/{userId}/verification", unverifiedUser.getId())
                .param("code", verificationCode)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(422));
	}
	
	@Test
	public void testEmailVerificationNonExistingUser() throws Exception {
		
		mvc.perform(post("/api/core/users/99/verification")
                .param("code", verificationCode)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(404));
	}
	
	@Test
	public void testEmailVerificationWrongToken() throws Exception {
		
		// null token
		mvc.perform(post("/api/core/users/{userId}/verification", unverifiedUser.getId())
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(400));

		// blank token
		mvc.perform(post("/api/core/users/{userId}/verification", unverifiedUser.getId())
                .param("code", "")
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(401));

		// Wrong audience
		String token = emailJwtService.createToken("wrong-audience",
				Long.toString(unverifiedUser.getId()), 60000L,
				LemonMapUtils.mapOf("email", UNVERIFIED_USER_EMAIL));
		mvc.perform(post("/api/core/users/{userId}/verification", unverifiedUser.getId())
                .param("code", token)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(401));
		
		// Wrong email
		token = emailJwtService.createToken(AbstractUserService.VERIFY_AUDIENCE,
				Long.toString(unverifiedUser.getId()), 60000L,
				LemonMapUtils.mapOf("email", "wrong.email@example.com"));
		mvc.perform(post("/api/core/users/{userId}/verification", unverifiedUser.getId())
                .param("code", token)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(403));

		// expired token
		token = emailJwtService.createToken(AbstractUserService.VERIFY_AUDIENCE,
				Long.toString(unverifiedUser.getId()), 1L,
				LemonMapUtils.mapOf("email", UNVERIFIED_USER_EMAIL));
		// Thread.sleep(1001L);
		mvc.perform(post("/api/core/users/{userId}/verification", unverifiedUser.getId())
                .param("code", token)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(401));
	}
	
	@Test
	public void testEmailVerificationAfterCredentialsUpdate() throws Exception {
		
		// Credentials updated after the verification token is issued
//		Thread.sleep(1L);
		AbstractUser<Long> user = (AbstractUser<Long>) unsecuredUserService.findById(unverifiedUser.getId()).get();
		user.setCredentialsUpdatedMillis(System.currentTimeMillis());
		unsecuredUserService.save(user);

		Thread.sleep(300);
		mvc.perform(post("/api/core/users/{userId}/verification", unverifiedUser.getId())
                .param("code", verificationCode)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(401));
	}
}
