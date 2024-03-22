package com.github.vincemann.springrapid.authtests.tests;

import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import com.github.vincemann.springrapid.core.util.AopProxyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RequestContactInformationChangeTest extends AuthIntegrationTest {


	protected RequestContactInformationChangeDto contactInformationChangeDto(String oldContactInformation) {

		RequestContactInformationChangeDto changeForm = new RequestContactInformationChangeDto();
//		changeForm.setPassword(USER_PASSWORD);
		changeForm.setNewContactInformation(NEW_CONTACT_INFORMATION);
		changeForm.setOldContactInformation(oldContactInformation);
		return changeForm;
	}

	@Test
	public void unverifiedUserCanRequestContactInformationChange() throws Exception {
		AbstractUser<?> unverifiedUser = testAdapter.createUnverifiedUser();
		String token = userController.login2xx(UNVERIFIED_USER_CONTACT_INFORMATION,UNVERIFIED_USER_PASSWORD);
		RequestContactInformationChangeDto dto = contactInformationChangeDto(UNVERIFIED_USER_CONTACT_INFORMATION);
		mvc.perform(userController.requestContactInformationChange(dto,token))
				.andExpect(status().is(204));

		verify(AopProxyUtils.getUltimateTargetObject(msgSender)).send(any());

		AbstractUser<?> updatedUser = testAdapter.fetchUser(unverifiedUser.getContactInformation());
		Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getNewContactInformation());
		Assertions.assertEquals(UNVERIFIED_USER_CONTACT_INFORMATION, updatedUser.getContactInformation());
	}

	@Test
	public void userCanRequestContactInformationChange() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		String token = userController.login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		RequestContactInformationChangeDto dto = contactInformationChangeDto(USER_CONTACT_INFORMATION);
		mvc.perform(userController.requestContactInformationChange(dto,token))
				.andExpect(status().is(204));

		verify(AopProxyUtils.getUltimateTargetObject(msgSender)).send(any());

		AbstractUser<?> updatedUser = testAdapter.fetchUser(user.getContactInformation());
		Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getNewContactInformation());
		Assertions.assertEquals(USER_CONTACT_INFORMATION, updatedUser.getContactInformation());
	}

	/**
     * A admin should be able to request changing contactInformation of another user.
     */
	@Test
	public void adminCanRequestContactInformationChangeOfDiffUser() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		AbstractUser<?> admin = testAdapter.createAdmin();
		String token = userController.login2xx(ADMIN_CONTACT_INFORMATION,ADMIN_PASSWORD);
		RequestContactInformationChangeDto dto = contactInformationChangeDto(USER_CONTACT_INFORMATION);
		mvc.perform(userController.requestContactInformationChange(dto,token))
				.andExpect(status().is(204));

		AbstractUser<?> updatedUser = testAdapter.fetchUser(user.getContactInformation());
		Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getNewContactInformation());
	}	
	
	/**
     * A request changing contactInformation of unknown user.
     */
	@Test
	public void cantRequestContactInformationChangeOfUnknownUser() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		String token = userController.login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		RequestContactInformationChangeDto dto = contactInformationChangeDto(UNKNOWN_CONTACT_INFORMATION);
		mvc.perform(userController.requestContactInformationChange(dto,token))
				.andExpect(status().is(404));
		
		verifyNoMsgSent();
	}

	@Test
	public void userCantRequestContactInformationChangeOfDiffUser() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		AbstractUser<?> secondUser = testAdapter.createSecondUser();
		String token = userController.login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		RequestContactInformationChangeDto dto = contactInformationChangeDto(SECOND_USER_CONTACT_INFORMATION);
		mvc.perform(userController.requestContactInformationChange(dto,token))
				.andExpect(status().is(403));
		
		verifyNoMsgSent();

		AbstractUser<?> updatedUser = testAdapter.fetchUser(secondUser.getContactInformation());
		Assertions.assertNull(updatedUser.getNewContactInformation());
	}


	@Test
	public void cantRequestContactInformationChangeWithInvalidData() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		AbstractUser<?> secondUser = testAdapter.createSecondUser();

		RequestContactInformationChangeDto dto = new RequestContactInformationChangeDto();
		dto.setNewContactInformation(null);
		dto.setOldContactInformation(USER_CONTACT_INFORMATION);
//		dto.setPassword(null);
		// try with null newContactInformation
		String token = userController.login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		mvc.perform(userController.requestContactInformationChange(dto,token))
				.andExpect(status().is(400));
		verifyNoMsgSent();
    	
		dto = new RequestContactInformationChangeDto();
//		dto.setPassword("");
		dto.setOldContactInformation(USER_CONTACT_INFORMATION);
		dto.setNewContactInformation("");
		
    	// try with blank newContactInformation
		mvc.perform(userController.requestContactInformationChange(dto,token))
				.andExpect(status().is(400));
		verifyNoMsgSent();

		// try with invalid newContactInformation
		dto = new RequestContactInformationChangeDto();
		dto.setNewContactInformation(INVALID_CONTACT_INFORMATION);
		dto.setOldContactInformation(USER_CONTACT_INFORMATION);


		mvc.perform(userController.requestContactInformationChange(dto,token))
				.andExpect(status().is(400));
		verifyNoMsgSent();
		// try with an existing contactInformation
		dto = contactInformationChangeDto(USER_CONTACT_INFORMATION);
		dto.setNewContactInformation(SECOND_USER_CONTACT_INFORMATION);
		mvc.perform(userController.requestContactInformationChange(dto,token))
				.andExpect(status().is(400));

		verifyNoMsgSent();
	}
}
