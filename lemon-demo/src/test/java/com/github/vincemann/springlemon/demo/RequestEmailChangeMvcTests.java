package com.github.vincemann.springlemon.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springlemon.demo.domain.User;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springrapid.core.util.MapperUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RequestEmailChangeMvcTests extends AbstractMvcTests {
	
	private static final String NEW_EMAIL = "new.email@example.com";
	
	private RequestEmailChangeForm form() {

		RequestEmailChangeForm changeForm = new RequestEmailChangeForm();
		changeForm.setPassword(USER_PASSWORD);
		changeForm.setNewEmail(NEW_EMAIL);
		
		return changeForm;
	}

	@Test
	public void testRequestEmailChange() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/email-change-request", UNVERIFIED_USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(UNVERIFIED_USER_ID))
				.content(MapperUtils.toJson(form())))
				.andExpect(status().is(204));
		
		verify(mailSender).send(any());
		
		User updatedUser = userRepository.findById(UNVERIFIED_USER_ID).get();
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getNewEmail());
		Assertions.assertEquals(UNVERIFIED_USER_EMAIL, updatedUser.getEmail());
	}
	
	/**
     * A good admin should be able to request changing email of another user.
     */
	@Test
	public void testGoodAdminRequestEmailChange() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/email-change-request", UNVERIFIED_USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(ADMIN_ID))
				.content(MapperUtils.toJson(form())))
				.andExpect(status().is(204));
		
		User updatedUser = userRepository.findById(UNVERIFIED_USER_ID).get();
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getNewEmail());
	}	
	
	/**
     * A request changing email of unknown user.
     */
	@Test
	public void testRequestEmailChangeUnknownUser() throws Exception {
		
		mvc.perform(post("/api/core/users/99/email-change-request")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(ADMIN_ID))
				.content(MapperUtils.toJson(form())))
				.andExpect(status().is(404));
		
		verify(mailSender, never()).send(any());
	}

	/**
	 * A non-admin should not be able to request changing
	 * the email id of another user
	 */
	@Test
	public void testNonAdminRequestEmailChangeAnotherUser() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/email-change-request", ADMIN_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(USER_ID))
				.content(MapperUtils.toJson(form())))
				.andExpect(status().is(403));
		
		verify(mailSender, never()).send(any());

		User updatedUser = userRepository.findById(UNVERIFIED_USER_ID).get();
		Assertions.assertNull(updatedUser.getNewEmail());
	}
	
	/**
	 * A bad admin trying to change the email id
	 * of another user
	 */
	@Test
	public void testBadAdminRequestEmailChangeAnotherUser() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/email-change-request", ADMIN_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(UNVERIFIED_ADMIN_ID))
				.content(MapperUtils.toJson(form())))
				.andExpect(status().is(403));
		
		verify(mailSender, never()).send(any());
	}

	/**
     * Trying with invalid data.
	 * @throws Exception 
	 * @throws JsonProcessingException 
     */
	@Test
	public void tryingWithInvalidData() throws JsonProcessingException, Exception {
		RequestEmailChangeForm form = form();
		form.setNewEmail(null);
		form.setPassword(null);
		// try with null newEmail and password
		mvc.perform(post("/api/core/users/{id}/email-change-request", UNVERIFIED_USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(UNVERIFIED_USER_ID))
				.content(MapperUtils.toJson(form)))
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.errors[*].field").value(hasSize(2)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
						"emailChangeForm.newEmail",
						"emailChangeForm.password")));
    	
		RequestEmailChangeForm emailChangeForm = new RequestEmailChangeForm();
		emailChangeForm.setPassword("");
		emailChangeForm.setNewEmail("");
		
    	// try with blank newEmail and password
		mvc.perform(post("/api/core/users/{id}/email-change-request", UNVERIFIED_USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(UNVERIFIED_USER_ID))
				.content(MapperUtils.toJson(emailChangeForm)))
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.errors[*].field").value(hasSize(4)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
						"emailChangeForm.newEmail",
						"emailChangeForm.password")));

		// try with invalid newEmail
		emailChangeForm = form();
		emailChangeForm.setNewEmail("an-invalid-email");
		mvc.perform(post("/api/core/users/{id}/email-change-request", UNVERIFIED_USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(UNVERIFIED_USER_ID))
				.content(MapperUtils.toJson(emailChangeForm)))
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems("emailChangeForm.newEmail")));

		// try with wrong password
		emailChangeForm = form();
		emailChangeForm.setPassword("wrong-password");
		mvc.perform(post("/api/core/users/{id}/email-change-request", UNVERIFIED_USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(UNVERIFIED_USER_ID))
				.content(MapperUtils.toJson(emailChangeForm)))
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems("updatedUser.password")));

		// try with null password
		emailChangeForm = form();
		emailChangeForm.setPassword(null);
		mvc.perform(post("/api/core/users/{id}/email-change-request", UNVERIFIED_USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(UNVERIFIED_USER_ID))
				.content(MapperUtils.toJson(emailChangeForm)))
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems("emailChangeForm.password")));

		// try with an existing email
		emailChangeForm = form();
		emailChangeForm.setNewEmail(ADMIN_EMAIL);;
		mvc.perform(post("/api/core/users/{id}/email-change-request", UNVERIFIED_USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(UNVERIFIED_USER_ID))
				.content(MapperUtils.toJson(emailChangeForm)))
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems("emailChangeForm.newEmail")));
		
		verify(mailSender, never()).send(any());
	}
}
