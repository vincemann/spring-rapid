package com.github.vincemann.springlemon.demo;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.service.AbstractUserService;
import com.github.vincemann.springlemon.auth.service.token.EmailJwtService;
import com.github.vincemann.springlemon.auth.util.LemonMapUtils;
import com.github.vincemann.springlemon.demo.domain.User;
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
	public void setUp() {

		AbstractUser<Long> user = userRepository.findById(unverifiedUser.getId()).get();
		user.setNewEmail(NEW_EMAIL);

//		securityContext.login(principalUserConverter.toPrincipal(user));

		userRepository.save(user);

//		securityContext.logout();

		changeEmailCode = emailJwtService.createToken(
				AbstractUserService.CHANGE_EMAIL_AUDIENCE,
				Long.toString(unverifiedUser.getId()),
				60000L,
				LemonMapUtils.mapOf("newEmail", NEW_EMAIL));
	}

//	@Autowired
//	RapidSecurityContext securityContext;
//
//	@Autowired
//	LemonPrincipalUserConverter principalUserConverter;

	@Test
	public void testChangeEmail() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/email", unverifiedUser.getId())
                .param("code", changeEmailCode)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(200))
				//gets new token for new email to use
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(unverifiedUser.getId()));
		
		AbstractUser<Long> updatedUser = userRepository.findById(unverifiedUser.getId()).get();
		Assertions.assertNull(updatedUser.getNewEmail());
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getEmail());
		
		// Shouldn't be able to login with old token
		mvc.perform(post("/api/core/users/{id}/email", unverifiedUser.getId())
                .param("code", changeEmailCode)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(401));
	}
	
    /**
     * Providing a wrong changeEmailCode shouldn't work.
     */
	@Test
	public void testChangeEmail_withWrongCode() throws Exception {
		
		// Blank token
		mvc.perform(post("/api/core/users/{id}/email", unverifiedUser.getId())
                .param("code", "")
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(422));

		// Wrong audience
		String code = emailJwtService.createToken(
				"", // blank audience
				Long.toString(unverifiedUser.getId()), 60000L,
				LemonMapUtils.mapOf("newEmail", NEW_EMAIL));
		
		mvc.perform(post("/api/core/users/{id}/email", unverifiedUser.getId())
                .param("code", code)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(401));

		// Wrong userId subject
		code = emailJwtService.createToken(
				AbstractUserService.CHANGE_EMAIL_AUDIENCE,
				Long.toString(admin.getId()), 60000L,
				LemonMapUtils.mapOf("newEmail", NEW_EMAIL));
		
		mvc.perform(post("/api/core/users/{id}/email", unverifiedUser.getId())
                .param("code", code)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(403));
		
		// Wrong new email
		code = emailJwtService.createToken(
				AbstractUserService.CHANGE_EMAIL_AUDIENCE,
				Long.toString(unverifiedUser.getId()), 60000L,
				LemonMapUtils.mapOf("newEmail", "wrong.new.email@example.com"));
		
		mvc.perform(post("/api/core/users/{id}/email", unverifiedUser.getId())
                .param("code", code)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(403));
	}
	
    /**
     * Providing an obsolete changeEmailCode shouldn't work.
     */
	@Test
	public void testChangeEmailObsoleteCode() throws Exception {

		// credentials updated after the request for email change was made
		Thread.sleep(1L);
		AbstractUser<Long> user = userRepository.findById(unverifiedUser.getId()).get();
		user.setCredentialsUpdatedMillis(System.currentTimeMillis());
		userRepository.save(user);
		
		// A new auth token is needed, because old one would be obsolete!
		String authToken = login(UNVERIFIED_USER_EMAIL, USER_PASSWORD);
		
		// now ready to test!
		mvc.perform(post("/api/core/users/{id}/email", unverifiedUser.getId())
                .param("code", changeEmailCode)
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

		mvc.perform(post("/api/core/users/{id}/email", user.getId())
                .param("code", changeEmailCode)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(user.getId()))
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
		AbstractUser<Long> user = userRepository.findById(admin.getId()).get();
		user.setEmail(NEW_EMAIL);
		userRepository.save(user);
		
		mvc.perform(post("/api/core/users/{id}/email", unverifiedUser.getId())
                .param("code", changeEmailCode)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(422));
	}
}
