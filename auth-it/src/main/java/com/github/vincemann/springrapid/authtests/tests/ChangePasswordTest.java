package com.github.vincemann.springrapid.authtests.tests;

import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.containsString;
import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChangePasswordTest extends AuthIntegrationTest {

	private ChangePasswordDto changePasswordDto(String contactInformation, String oldPassword){
		return ChangePasswordDto.Builder.builder()
				.contactInformation(contactInformation)
				.oldPassword(oldPassword)
				.newPassword(NEW_PASSWORD)
				.build();
	}

	@Test
	public void userCanChangeOwnPassword() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		ChangePasswordDto changePasswordDto = ChangePasswordDto.Builder.builder()
				.contactInformation(USER_CONTACT_INFORMATION)
				.oldPassword(USER_PASSWORD)
				.newPassword(NEW_PASSWORD)
				.build();

		String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.changePassword(changePasswordDto,token))
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));

		// old password does not work anymore
		mvc.perform(userController.login(USER_CONTACT_INFORMATION, USER_PASSWORD))
				.andExpect(status().isUnauthorized());
		// Ensure able to login with new password
		userController.login2xx(USER_CONTACT_INFORMATION, NEW_PASSWORD);
	}
	

	@Test
	public void adminCanChangePasswordOfDiffUser() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		AbstractUser<?> admin = testAdapter.createAdmin();
		ChangePasswordDto changePasswordDto = changePasswordDto(USER_CONTACT_INFORMATION,USER_PASSWORD);

		String token = userController.login2xx(ADMIN_CONTACT_INFORMATION, ADMIN_PASSWORD);
		mvc.perform(userController.changePassword(changePasswordDto,token))
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")));

		// Ensure able to login with new password
		userController.login2xx(USER_CONTACT_INFORMATION, NEW_PASSWORD);
	}


	@Test
	public void cantChangePasswordForUnknownId() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		ChangePasswordDto changePasswordDto = changePasswordDto(UNKNOWN_CONTACT_INFORMATION,USER_PASSWORD);

		String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		mvc.perform(userController.changePassword(changePasswordDto,token))
				.andExpect(status().isNotFound());
	}


	@Test
	public void userCantChangePasswordOfAnotherUser() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		AbstractUser<?> secondUser = testAdapter.createSecondUser();

		ChangePasswordDto changePasswordDto = changePasswordDto(SECOND_USER_CONTACT_INFORMATION,SECOND_USER_PASSWORD);

		String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		mvc.perform(userController.changePassword(changePasswordDto,token))
				.andExpect(status().isForbidden());

		userController.login2xx(SECOND_USER_CONTACT_INFORMATION, SECOND_USER_PASSWORD);
	}


	@Test
	public void cantChangeOwnPasswordWithInvalidData() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();

		// All fields null
		ChangePasswordDto dto = ChangePasswordDto.Builder.builder()
				.oldPassword(null)
				.newPassword(null)
				.contactInformation(null)
				.build();
		String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.changePassword(dto,token))
				.andExpect(status().isBadRequest());
		userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);


		// invalid pw
		dto = ChangePasswordDto.Builder.builder()
				.oldPassword(USER_PASSWORD)
				.newPassword(INVALID_PASSWORD)
				.contactInformation(USER_CONTACT_INFORMATION)
				.build();
		mvc.perform(userController.changePassword(dto,token))
				.andExpect(status().isBadRequest());
		userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		// wrong old password
		dto = ChangePasswordDto.Builder.builder()
				.oldPassword(USER_PASSWORD+"wrong")
				.newPassword(NEW_PASSWORD)
				.contactInformation(USER_CONTACT_INFORMATION)
				.build();
		mvc.perform(userController.changePassword(dto,token))
				.andExpect(status().isBadRequest());
		userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);

		// blank contact information
		// wrong old password
		dto = ChangePasswordDto.Builder.builder()
				.oldPassword(USER_PASSWORD)
				.newPassword(NEW_PASSWORD)
				.contactInformation("")
				.build();
		mvc.perform(userController.changePassword(dto,token))
				.andExpect(status().isBadRequest());
		userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
	}
}
