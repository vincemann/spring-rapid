package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.dto.ChangePasswordDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChangePasswordTest extends AbstractRapidAuthIntegrationTest {
	
	private static final String NEW_PASSWORD = "a-new-password123";

	private ChangePasswordDto changePasswordDto(String oldPassword){
		return ChangePasswordDto.builder()
				.oldPassword(oldPassword)
				.password(NEW_PASSWORD)
				.retypePassword(NEW_PASSWORD)
				.build();
	}

	/**
	 * A non-admin user should be able to change his password.
	 */
	@Test
	public void canChangeOwnPassword() throws Exception {
		ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
				.oldPassword(USER_PASSWORD)
				.password(NEW_PASSWORD)
				.retypePassword(NEW_PASSWORD)
				.build();

		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		testTemplate.changePassword(getUser().getId(),token, changePasswordDto)
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));

		// old password does not work anymore
		login(USER_EMAIL, USER_PASSWORD)
				.andExpect(status().isUnauthorized());
		// Ensure able to login with new password
		login2xx(USER_EMAIL, NEW_PASSWORD);
	}
	

	@Test
	public void adminCanChangePasswordOfDiffUser() throws Exception {
		ChangePasswordDto changePasswordDto = changePasswordDto(USER_PASSWORD);

		String token = login2xx(ADMIN_EMAIL, ADMIN_PASSWORD);
		testTemplate.changePassword(getUser().getId(),token, changePasswordDto)
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));

		// Ensure able to login with new password
		login2xx(USER_EMAIL, NEW_PASSWORD);
	}


	@Test
	public void cantChangePasswordForUnknownId() throws Exception {
		ChangePasswordDto changePasswordDto = changePasswordDto(USER_PASSWORD);

		String token = login2xx(USER_EMAIL, USER_PASSWORD);

		testTemplate.changePassword(UNKNOWN_USER_ID,token, changePasswordDto)
				.andExpect(status().isNotFound());
	}


	@Test
	public void userCantChangePasswordOfAnotherUser() throws Exception {
		ChangePasswordDto changePasswordDto = changePasswordDto(SECOND_USER_PASSWORD);

		String token = login2xx(USER_EMAIL, USER_PASSWORD);

		testTemplate.changePassword(getSecondUser().getId(),token, changePasswordDto)
				.andExpect(status().isForbidden());

		login2xx(SECOND_USER_EMAIL, SECOND_USER_PASSWORD);
	}


	@Test
	public void cantChangeOwnPasswordWithInvalidData() throws Exception {

		// All fields null
		ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
				.oldPassword(null)
				.password(null)
				.retypePassword(null)
				.build();
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		testTemplate.changePassword(getUser().getId(),token, changePasswordDto)
				.andExpect(status().isBadRequest());
		login2xx(USER_EMAIL, USER_PASSWORD);


		// All fields too short
		changePasswordDto = ChangePasswordDto.builder()
				.oldPassword("short")
				.password("short")
				.retypePassword("short")
				.build();
		token = login2xx(USER_EMAIL, USER_PASSWORD);
		testTemplate.changePassword(getUser().getId(),token, changePasswordDto)
				.andExpect(status().isBadRequest());
		login2xx(USER_EMAIL, USER_PASSWORD);

		// different retype-password
		changePasswordDto = ChangePasswordDto.builder()
				.oldPassword(USER_PASSWORD)
				.password(NEW_PASSWORD)
				.retypePassword(NEW_PASSWORD+"different")
				.build();
		token = login2xx(USER_EMAIL, USER_PASSWORD);
		testTemplate.changePassword(getUser().getId(),token, changePasswordDto)
				.andExpect(status().isBadRequest());
		login2xx(USER_EMAIL, USER_PASSWORD);
	}
}
