package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.LemonMapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChangeEmailAuthTest extends AbstractRapidAuthTest {
	
	private static final String NEW_EMAIL = "new.email@example.com";

	private String changeEmailCode;
	
	@Autowired
	private JweTokenService jweTokenService;

	@Autowired
	private AbstractUserRepository userRepository;

	@Override
	protected void createTestUsers() throws Exception{
		super.createTestUsers();
		AbstractUser<Long> user = getUserService().findById(getUnverifiedUser().getId()).get();
		user.setNewEmail(NEW_EMAIL);
		userRepository.save(user);
	}

	@BeforeEach
	protected void setupEmailCode() throws Exception {
		changeEmailCode = jweTokenService.createToken(
				RapidJwt.create(
				AbstractUserService.CHANGE_EMAIL_AUDIENCE,
				Long.toString(getUnverifiedUser().getId()),
				600000L,
				LemonMapUtils.mapOf("newEmail", NEW_EMAIL)));

	}

	//works solo but token is obsolete when run in group
//	@Disabled
	@Test
	public void testChangeEmail() throws Exception {
		
		mvc.perform(post(authProperties.getController().getChangeEmailUrl())
                .param("code", changeEmailCode)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(200))
				//gets new token for new email to use
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(getUnverifiedUser().getId()));
		
		AbstractUser<Long> updatedUser = getUserService().findById(getUnverifiedUser().getId()).get();
		Assertions.assertNull(updatedUser.getNewEmail());
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getEmail());
		
		// Shouldn't be able to login with old token
		mvc.perform(post(authProperties.getController().getChangeEmailUrl())
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
		mvc.perform(post(authProperties.getController().getChangeEmailUrl())
                .param("code", "")
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(422));

		// Wrong audience
		String code = jweTokenService.createToken(
				RapidJwt.create(
				"", // blank audience
				Long.toString(getUnverifiedUser().getId()), 60000L,
				LemonMapUtils.mapOf("newEmail", NEW_EMAIL)));
		
		mvc.perform(post(authProperties.getController().getChangeEmailUrl())
                .param("code", code)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(403));

		// Wrong userId subject
		code = jweTokenService.createToken(
				RapidJwt.create(
				AbstractUserService.CHANGE_EMAIL_AUDIENCE,
				Long.toString(getAdmin().getId()), 60000L,
				LemonMapUtils.mapOf("newEmail", NEW_EMAIL)));
		
		mvc.perform(post(authProperties.getController().getChangeEmailUrl())
                .param("code", code)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(403));
		
		// Wrong new email
		code = jweTokenService.createToken(
				RapidJwt.create(
				AbstractUserService.CHANGE_EMAIL_AUDIENCE,
				Long.toString(getUnverifiedUser().getId()), 60000L,
				LemonMapUtils.mapOf("newEmail", "wrong.new.email@example.com")));
		
		mvc.perform(post(authProperties.getController().getChangeEmailUrl())
                .param("code", code)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(403));
	}
	
    /**
     * Providing an obsolete changeEmailCode shouldn't work.
     */
    //todo sometimes 401 sometimes 403
	@Test
	public void testChangeEmailObsoleteCode() throws Exception {

		// credentials updated after the request for email change was made
//		Thread.sleep(1L);
		AbstractUser<Long> user = getUserService().findById(getUnverifiedUser().getId()).get();
		user.setCredentialsUpdatedMillis(System.currentTimeMillis());
		userRepository.save(user);

//		Thread.sleep(1L);

		// A new auth token is needed, because old one would be obsolete!
		String authToken = successful_login(UNVERIFIED_USER_EMAIL, UNVERIFIED_USER_PASSWORD);


		// now ready to test!
		mvc.perform(post(authProperties.getController().getChangeEmailUrl())
                .param("code", changeEmailCode)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, authToken)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(403));
	}
	
	/**
     * Trying without having requested first.
	 * @throws Exception 
     */
	@Test
	public void testChangeEmailWithoutAnyRequest() throws Exception {

		mvc.perform(post(authProperties.getController().getChangeEmailUrl(), getUser().getId())
                .param("code", changeEmailCode)
				.param("id",getUser().getId().toString())
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
		AbstractUser<Long> user = getUserService().findById(getAdmin().getId()).get();
		user.setEmail(NEW_EMAIL);
		userRepository.save(user);
		
		mvc.perform(post(authProperties.getController().getChangeEmailUrl(), getUnverifiedUser().getId())
                .param("code", changeEmailCode)
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
		        .andExpect(status().is(422));
	}
}
