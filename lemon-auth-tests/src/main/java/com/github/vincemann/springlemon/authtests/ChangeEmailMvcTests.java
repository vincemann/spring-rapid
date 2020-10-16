package com.github.vincemann.springlemon.authtests;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.service.AbstractUserService;
import com.github.vincemann.springlemon.auth.service.token.EmailJwtService;
import com.github.vincemann.springlemon.auth.util.LemonMapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChangeEmailMvcTests extends AbstractMvcTests {
	
	private static final String NEW_EMAIL = "new.email@example.com";

	private String changeEmailCode;
	
	@Autowired
	private EmailJwtService emailJwtService;

	@BeforeEach
	public void setup() throws Exception {
		configureMvc();
		System.err.println("creating test users");
		createTestUsers();
		System.err.println("test users created");

		AbstractUser<Long> user = getUnsecuredUserService().findById(getUnverifiedUser().getId()).get();
		user.setNewEmail(NEW_EMAIL);

		getUnsecuredUserService().update(user);

		System.err.println("logging in test users");
		loginTestUsers();
		System.err.println("test users logged in");


		changeEmailCode = emailJwtService.createToken(
				AbstractUserService.CHANGE_EMAIL_AUDIENCE,
				Long.toString(getUnverifiedUser().getId()),
				600000L,
				LemonMapUtils.mapOf("newEmail", NEW_EMAIL));

		setupSpies();
	}

	//works solo but token is obsolete when run in group
//	@Disabled
	@Test
	public void testChangeEmail() throws Exception {
		
		mvc.perform(post(lemonProperties.getController().getChangeEmailUrl())
                .param("code", changeEmailCode)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(200))
				//gets new token for new email to use
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(getUnverifiedUser().getId()));
		
		AbstractUser<Long> updatedUser = getUnsecuredUserService().findById(getUnverifiedUser().getId()).get();
		Assertions.assertNull(updatedUser.getNewEmail());
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getEmail());
		
		// Shouldn't be able to login with old token
		mvc.perform(post(lemonProperties.getController().getChangeEmailUrl())
                .param("code", changeEmailCode)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(401));
	}
	
    /**
     * Providing a wrong changeEmailCode shouldn't work.
     */
	@Test
	public void testChangeEmail_withWrongCode() throws Exception {
		
		// Blank token
		mvc.perform(post(lemonProperties.getController().getChangeEmailUrl())
                .param("code", "")
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(422));

		// Wrong audience
		String code = emailJwtService.createToken(
				"", // blank audience
				Long.toString(getUnverifiedUser().getId()), 60000L,
				LemonMapUtils.mapOf("newEmail", NEW_EMAIL));
		
		mvc.perform(post(lemonProperties.getController().getChangeEmailUrl())
                .param("code", code)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(401));

		// Wrong userId subject
		code = emailJwtService.createToken(
				AbstractUserService.CHANGE_EMAIL_AUDIENCE,
				Long.toString(getAdmin().getId()), 60000L,
				LemonMapUtils.mapOf("newEmail", NEW_EMAIL));
		
		mvc.perform(post(lemonProperties.getController().getChangeEmailUrl())
                .param("code", code)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(403));
		
		// Wrong new email
		code = emailJwtService.createToken(
				AbstractUserService.CHANGE_EMAIL_AUDIENCE,
				Long.toString(getUnverifiedUser().getId()), 60000L,
				LemonMapUtils.mapOf("newEmail", "wrong.new.email@example.com"));
		
		mvc.perform(post(lemonProperties.getController().getChangeEmailUrl())
                .param("code", code)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(403));
	}
	
    /**
     * Providing an obsolete changeEmailCode shouldn't work.
     */
	@Test
	public void testChangeEmailObsoleteCode() throws Exception {

		// credentials updated after the request for email change was made
//		Thread.sleep(1L);
		AbstractUser<Long> user = getUnsecuredUserService().findById(getUnverifiedUser().getId()).get();
		user.setCredentialsUpdatedMillis(System.currentTimeMillis());
		getUnsecuredUserService().update(user);

		Thread.sleep(1L);

		// A new auth token is needed, because old one would be obsolete!
		String authToken = successful_login(UNVERIFIED_USER_EMAIL, UNVERIFIED_USER_PASSWORD);
		
		// now ready to test!
		mvc.perform(post(lemonProperties.getController().getChangeEmailUrl())
                .param("code", changeEmailCode)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, authToken)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(401));	
	}
	
	/**
     * Trying without having requested first.
	 * @throws Exception 
     */
	@Test
	public void testChangeEmailWithoutAnyRequest() throws Exception {

		mvc.perform(post(lemonProperties.getController().getChangeEmailUrl(), getUser().getId())
                .param("code", changeEmailCode)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(422));
	}
	
    /**
     * Trying after some user registers the newEmail, leaving it non unique.
     * @throws Exception 
     */
	@Test
	public void testChangeEmailNonUniqueEmail() throws Exception {
		
		// Some other user changed to the same email
		AbstractUser<Long> user = getUnsecuredUserService().findById(getAdmin().getId()).get();
		user.setEmail(NEW_EMAIL);
		getUnsecuredUserService().update(user);
		
		mvc.perform(post(lemonProperties.getController().getChangeEmailUrl(), getUnverifiedUser().getId())
                .param("code", changeEmailCode)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(422));
	}
}
