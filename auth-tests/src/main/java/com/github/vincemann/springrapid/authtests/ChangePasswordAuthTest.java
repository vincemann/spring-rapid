package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.core.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChangePasswordAuthTest extends AbstractRapidAuthTest {
	
	private static final String NEW_PASSWORD = "a-new-password123";
	
	private ChangePasswordDto changePasswordForm(String oldPassword) {
		
		ChangePasswordDto form = new ChangePasswordDto();
		form.setOldPassword(oldPassword);
		form.setPassword(NEW_PASSWORD);
		form.setRetypePassword(NEW_PASSWORD);
		
		return form;		
	}

	/**
	 * A non-admin user should be able to change his password.
	 */
	@Test
	public void canChangeOwnPassword() throws Exception {
		
		mvc.perform(post(authProperties.getController().getChangePasswordUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(changePasswordForm(UNVERIFIED_USER_PASSWORD))))
				.andExpect(status().is(204))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));
		
		// Ensure able to login with new password
		successful_login(UNVERIFIED_USER_EMAIL, NEW_PASSWORD);
	}
	
	/**
	 * An good admin user should be able to change the password of another user.
	 */
	@Test
	public void adminCanChangePasswordOfDiffUser() throws Exception {
		
		mvc.perform(post(authProperties.getController().getChangePasswordUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(changePasswordForm(UNVERIFIED_USER_PASSWORD))))
				.andExpect(status().is(204))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));
		
		// Ensure able to login with new password
		successful_login(UNVERIFIED_USER_EMAIL, NEW_PASSWORD);
	}
	
	/**
	 * Providing an unknown id should return 404.
	 */
	@Test
	public void cantChangePasswordForUnknownId() throws Exception {
		
		mvc.perform(post(authProperties.getController().getChangePasswordUrl())
				.param("id",UNKNOWN_USER_ID)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(changePasswordForm(ADMIN_PASSWORD))))
				.andExpect(status().is(404));
	}
	
	/**
	 * A non-admin user should not be able to change others' password.
	 */
	@Test
	public void cantChangePasswordOfAnotherUser() throws Exception {
		
		mvc.perform(post(authProperties.getController().getChangePasswordUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUser().getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(changePasswordForm(UNVERIFIED_USER_PASSWORD))))
				.andExpect(status().is(403));
		
		// Ensure password didn't change
		successful_login(UNVERIFIED_USER_EMAIL, UNVERIFIED_USER_PASSWORD);
	}

//	/**
//	 * A  bad admin user should not be able to change others' password.
//	 */
//	@Test
//	public void testBadAdminChangePasswordOfAnotherUser_shouldFail() throws Exception {
//
//		mvc.perform(post("/api/core/users/{id}/password", getUnverifiedUser().getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(secondAdmin.getId()))
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(MapperUtils.toJson(changePasswordForm(ADMIN_PASSWORD))))
//				.andExpect(status().is(403));
//
//		// Ensure password didn't change
//		successful_login(UNVERIFIED_USER_EMAIL, UNVERIFIED_USER_PASSWORD);
//	}
	
	@Test
	public void cantChangeOwnPasswordWithInvalidData() throws Exception {
		
		// All fields null
		mvc.perform(post(authProperties.getController().getChangePasswordUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(new ChangePasswordDto())))
				.andExpect(status().is(400));
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(3)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
//						"changePasswordForm.oldPassword",
//						 "changePasswordForm.retypePassword",
//						 "changePasswordForm.password")));
		
		// All fields too short
		ChangePasswordDto form = new ChangePasswordDto();
		form.setOldPassword("short");
		form.setPassword("short");
		form.setRetypePassword("short");

		mvc.perform(post(authProperties.getController().getChangePasswordUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(form)))
				.andExpect(status().is(400));
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(3)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
//						"changePasswordForm.oldPassword",
//						 "changePasswordForm.retypePassword",
//						 "changePasswordForm.password")));
		
		// different retype-password
		form = changePasswordForm(UNVERIFIED_USER_PASSWORD);
		form.setRetypePassword("different-retype-password");

		mvc.perform(post(authProperties.getController().getChangePasswordUrl())
				.param("id",getUnverifiedUser().getId().toString())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(form)))
				.andExpect(status().is(400));
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(2)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
//						 "changePasswordForm.retypePassword",
//						 "changePasswordForm.password")));
	}
}
