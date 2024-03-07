package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.containsString;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChangePasswordTest extends RapidAuthIntegrationTest {

	private ChangePasswordDto changePasswordDto(String contactInformation, String oldPassword){
		return ChangePasswordDto.Builder.builder()
				.contactInformation(contactInformation)
				.oldPassword(oldPassword)
				.newPassword(NEW_PASSWORD)
				.build();
	}



	@Test
	public void canChangeOwnPassword() throws Exception {
		ChangePasswordDto changePasswordDto = ChangePasswordDto.Builder.builder()
				.contactInformation(USER_CONTACT_INFORMATION)
				.oldPassword(USER_PASSWORD)
				.newPassword(NEW_PASSWORD)
				.build();

		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.changePassword(token, changePasswordDto))
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));

		// old password does not work anymore
		mvc.perform(userController.login(USER_CONTACT_INFORMATION, USER_PASSWORD))
				.andExpect(status().isUnauthorized());
		// Ensure able to login with new password
		login2xx(USER_CONTACT_INFORMATION, NEW_PASSWORD);
	}
	

	@Test
	public void adminCanChangePasswordOfDiffUser() throws Exception {
		ChangePasswordDto changePasswordDto = changePasswordDto(USER_CONTACT_INFORMATION,USER_PASSWORD);

		String token = login2xx(ADMIN_CONTACT_INFORMATION, ADMIN_PASSWORD);
		mvc.perform(userController.changePassword(token, changePasswordDto))
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));

		// Ensure able to login with new password
		login2xx(USER_CONTACT_INFORMATION, NEW_PASSWORD);
	}


	@Test
	public void cantChangePasswordForUnknownId() throws Exception {
		ChangePasswordDto changePasswordDto = changePasswordDto(UNKNOWN_CONTACT_INFORMATION,USER_PASSWORD);

		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		mvc.perform(userController.changePassword(token, changePasswordDto))
				.andExpect(status().isNotFound());
	}


	@Test
	public void userCantChangePasswordOfAnotherUser() throws Exception {
		ChangePasswordDto changePasswordDto = changePasswordDto(SECOND_USER_CONTACT_INFORMATION,SECOND_USER_PASSWORD);

		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		mvc.perform(userController.changePassword(token, changePasswordDto))
				.andExpect(status().isForbidden());

		login2xx(SECOND_USER_CONTACT_INFORMATION, SECOND_USER_PASSWORD);
	}


	@Test
	public void cantChangeOwnPasswordWithInvalidData() throws Exception {

		// All fields null
		ChangePasswordDto changePasswordDto = ChangePasswordDto.Builder.builder()
				.oldPassword(null)
				.newPassword(null)
				.contactInformation(null)
				.build();
		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.changePassword(token, changePasswordDto))
				.andExpect(status().isBadRequest());
		login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);


		// invalid pw
		changePasswordDto = ChangePasswordDto.Builder.builder()
				.oldPassword(USER_PASSWORD)
				.newPassword(INVALID_PASSWORD)
				.contactInformation(USER_CONTACT_INFORMATION)
				.build();
		token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.changePassword(token, changePasswordDto))
				.andExpect(status().isBadRequest());
		login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		// wrong old password
		changePasswordDto = ChangePasswordDto.Builder.builder()
				.oldPassword(USER_PASSWORD+"wrong")
				.newPassword(NEW_PASSWORD)
				.contactInformation(USER_CONTACT_INFORMATION)
				.build();
		token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.changePassword(token, changePasswordDto))
				.andExpect(status().isBadRequest());
		login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		// blank contact information
		// wrong old password
		changePasswordDto = ChangePasswordDto.Builder.builder()
				.oldPassword(USER_PASSWORD)
				.newPassword(NEW_PASSWORD)
				.contactInformation("")
				.build();
		token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.changePassword(token, changePasswordDto))
				.andExpect(status().isBadRequest());
		login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
	}
}
