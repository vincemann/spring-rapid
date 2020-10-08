package com.github.vincemann.springlemon.authtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
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
//		changeForm.setPassword(USER_PASSWORD);
		changeForm.setNewEmail(NEW_EMAIL);
		
		return changeForm;
	}

	@Test
	public void testRequestEmailChange() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/email-change-request", unverifiedUser.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
				.content(MapperUtils.toJson(form())))
				.andExpect(status().is(204));
		
		verify(mailSender).send(any());

		AbstractUser<Long> updatedUser = (AbstractUser<Long>) userRepository.findById(unverifiedUser.getId()).get();
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getNewEmail());
		Assertions.assertEquals(UNVERIFIED_USER_EMAIL, updatedUser.getEmail());
	}
	
	/**
     * A admin should be able to request changing email of another user.
     */
	@Test
	public void testGoodAdminRequestEmailChange() throws Exception {
		
		mvc.perform(post("/api/core/users/{id}/email-change-request", unverifiedUser.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(admin.getId()))
				.content(MapperUtils.toJson(form())))
				.andExpect(status().is(204));

		AbstractUser<Long> updatedUser = (AbstractUser<Long>) userRepository.findById(unverifiedUser.getId()).get();
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getNewEmail());
	}	
	
	/**
     * A request changing email of unknown user.
     */
	@Test
	public void testRequestEmailChangeUnknownUser() throws Exception {
		
		mvc.perform(post("/api/core/users/99/email-change-request")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(admin.getId()))
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
		
		mvc.perform(post("/api/core/users/{id}/email-change-request", admin.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(user.getId()))
				.content(MapperUtils.toJson(form())))
				.andExpect(status().is(403));
		
		verify(mailSender, never()).send(any());

		AbstractUser<Long> updatedUser = (AbstractUser<Long>) userRepository.findById(unverifiedUser.getId()).get();
		Assertions.assertNull(updatedUser.getNewEmail());
	}
	

	@Test
	public void testAdmin_triesToRequestEmailChange_ofDifferentAdmin_shouldFail() throws Exception {

		//unverified admins are not treated differently than verified admins
		mvc.perform(post("/api/core/users/{id}/email-change-request", admin.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(secondAdmin.getId()))
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
//		form.setPassword(null);
		// try with null newEmail
		mvc.perform(post("/api/core/users/{id}/email-change-request", unverifiedUser.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
				.content(MapperUtils.toJson(form)))
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
						"emailChangeForm.newEmail"
						/*"emailChangeForm.password"*/)));
    	
		RequestEmailChangeForm emailChangeForm = new RequestEmailChangeForm();
//		emailChangeForm.setPassword("");
		emailChangeForm.setNewEmail("");
		
    	// try with blank newEmail
		mvc.perform(post("/api/core/users/{id}/email-change-request", unverifiedUser.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
				.content(MapperUtils.toJson(emailChangeForm)))
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.errors[*].field").value(hasSize(2)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
						"emailChangeForm.newEmail"
						/*"emailChangeForm.password"*/)));

		// try with invalid newEmail
		emailChangeForm = form();
		emailChangeForm.setNewEmail("an-invalid-email");
		mvc.perform(post("/api/core/users/{id}/email-change-request", unverifiedUser.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
				.content(MapperUtils.toJson(emailChangeForm)))
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems("emailChangeForm.newEmail")));

		// try with wrong password
//		emailChangeForm = form();
//		emailChangeForm.setPassword("wrong-password");
//		mvc.perform(post("/api/core/users/{id}/email-change-request", unverifiedUser.getId())
//				.contentType(MediaType.APPLICATION_JSON)
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
//				.content(MapperUtils.toJson(emailChangeForm)))
//				.andExpect(status().is(422))
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems("updatedUser.password")));

		// try with null password
//		emailChangeForm = form();
//		emailChangeForm.setPassword(null);
//		mvc.perform(post("/api/core/users/{id}/email-change-request", unverifiedUser.getId())
//				.contentType(MediaType.APPLICATION_JSON)
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
//				.content(MapperUtils.toJson(emailChangeForm)))
//				.andExpect(status().is(422))
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems("emailChangeForm.password")));

		// try with an existing email
		emailChangeForm = form();
		emailChangeForm.setNewEmail(ADMIN_EMAIL);;
		mvc.perform(post("/api/core/users/{id}/email-change-request", unverifiedUser.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
				.content(MapperUtils.toJson(emailChangeForm)))
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems("emailChangeForm.newEmail")));
		
		verify(mailSender, never()).send(any());
	}
}
