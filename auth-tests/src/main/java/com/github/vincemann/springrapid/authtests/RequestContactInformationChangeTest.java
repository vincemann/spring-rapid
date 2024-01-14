package com.github.vincemann.springrapid.authtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;
import static com.github.vincemann.springrapid.core.util.ProxyUtils.aopUnproxy;
public class RequestContactInformationChangeTest extends RapidAuthIntegrationTest {


	protected RequestContactInformationChangeDto contactInformationChangeDto() {

		RequestContactInformationChangeDto changeForm = new RequestContactInformationChangeDto();
//		changeForm.setPassword(USER_PASSWORD);
		changeForm.setNewContactInformation(NEW_CONTACT_INFORMATION);
		return changeForm;
	}

	@Test
	public void unverifiedUserCanRequestContactInformationChange() throws Exception {
		String token = login2xx(UNVERIFIED_USER_CONTACT_INFORMATION,UNVERIFIED_USER_PASSWORD);
		mvc.perform(testTemplate.requestContactInformationChange(getUnverifiedUser().getId(),token,contactInformationChangeDto()))
				.andExpect(status().is(204));

		verify(aopUnproxy(mailSender)).send(any());

		AbstractUser<Serializable> updatedUser = getUserService().findById(getUnverifiedUser().getId()).get();
		Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getNewContactInformation());
		Assertions.assertEquals(UNVERIFIED_USER_CONTACT_INFORMATION, updatedUser.getContactInformation());
	}

	@Test
	public void userCanRequestContactInformationChange() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		mvc.perform(testTemplate.requestContactInformationChange(getUser().getId(),token,contactInformationChangeDto()))
				.andExpect(status().is(204));

		verify(aopUnproxy(mailSender)).send(any());

		AbstractUser<Serializable> updatedUser = getUserService().findById(getUser().getId()).get();
		Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getNewContactInformation());
		Assertions.assertEquals(USER_CONTACT_INFORMATION, updatedUser.getContactInformation());
	}

	/**
     * A admin should be able to request changing contactInformation of another user.
     */
	@Test
	public void adminCanRequestContactInformationChangeOfDiffUser() throws Exception {
		String token = login2xx(ADMIN_CONTACT_INFORMATION,ADMIN_PASSWORD);
		mvc.perform(testTemplate.requestContactInformationChange(getUser().getId(),token,contactInformationChangeDto()))
				.andExpect(status().is(204));

		AbstractUser<Serializable> updatedUser = getUserService().findById(getUser().getId()).get();
		Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getNewContactInformation());
	}	
	
	/**
     * A request changing contactInformation of unknown user.
     */
	@Test
	public void cantRequestContactInformationChangeOfUnknownUser() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		mvc.perform(testTemplate.requestContactInformationChange(UNKNOWN_USER_ID,token,contactInformationChangeDto()))
				.andExpect(status().is(404));
		
		verify(aopUnproxy(mailSender), never()).send(any());
	}

	@Test
	public void userCantRequestContactInformationChangeOfDiffUser() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		mvc.perform(testTemplate.requestContactInformationChange(getSecondUser().getId(),token,contactInformationChangeDto()))
				.andExpect(status().is(403));
		
		verify(aopUnproxy(mailSender), never()).send(any());

		AbstractUser<Serializable> updatedUser = getUserService().findById(getSecondUser().getId()).get();
		Assertions.assertNull(updatedUser.getNewContactInformation());
	}
	

	@Test
	public void adminCantRequestContactInformationChangeOfDiffAdmin() throws Exception {
		//unverified admins are not treated differently than verified admins
		String token = login2xx(ADMIN_CONTACT_INFORMATION,ADMIN_PASSWORD);
		mvc.perform(testTemplate.requestContactInformationChange(getSecondAdmin().getId(),token,contactInformationChangeDto()))
				.andExpect(status().is(403));
		
		verify(aopUnproxy(mailSender), never()).send(any());

		AbstractUser<Serializable> updatedUser = getUserService().findById(getSecondAdmin().getId()).get();
		Assertions.assertNull(updatedUser.getNewContactInformation());
	}

	/**
     * Trying with invalid data.
	 * @throws Exception 
	 * @throws JsonProcessingException 
     */
	@Test
	public void cantRequestContactInformationChangeWithInvalidData() throws JsonProcessingException, Exception {
		RequestContactInformationChangeDto dto = new RequestContactInformationChangeDto();
		dto.setNewContactInformation(null);
//		dto.setPassword(null);
		// try with null newContactInformation
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		mvc.perform(testTemplate.requestContactInformationChange(getUser().getId(),token,dto))
				.andExpect(status().is(400));
		verify(aopUnproxy(mailSender), never()).send(any());
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
//						"dto.newContactInformation"
						/*"dto.password"*/
    	
		dto = new RequestContactInformationChangeDto();
//		dto.setPassword("");
		dto.setNewContactInformation("");
		
    	// try with blank newContactInformation
		mvc.perform(testTemplate.requestContactInformationChange(getUser().getId(),token,dto))
				.andExpect(status().is(400));
		verify(aopUnproxy(mailSender), never()).send(any());
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(2)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
//						"dto.newContactInformation"
//						/*"dto.password"*/)));

		// try with invalid newContactInformation
		dto = new RequestContactInformationChangeDto();
		dto.setNewContactInformation(INVALID_CONTACT_INFORMATION);


		// todo kann das nicht über die @Email annotation am getter klären, muss also programmatisch geschehen
		mvc.perform(testTemplate.requestContactInformationChange(getUser().getId(),token,dto))
				.andExpect(status().is(400));
		verify(aopUnproxy(mailSender), never()).send(any());
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems("dto.newContactInformation")));

		// try with wrong password
//		dto = dto();
//		dto.setPassword("wrong-password");
//		mvc.perform(post("/api/core/users/{id}/contactInformation-change-request", getUnverifiedUser().getId())
//				.contentType(MediaType.APPLICATION_JSON)
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
//				.content(MapperUtils.toJson(dto)))
//				.andExpect(status().is(400))
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems("updatedUser.password")));

		// try with null password
//		dto = dto();
//		dto.setPassword(null);
//		mvc.perform(post("/api/core/users/{id}/contactInformation-change-request", getUnverifiedUser().getId())
//				.contentType(MediaType.APPLICATION_JSON)
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
//				.content(MapperUtils.toJson(dto)))
//				.andExpect(status().is(400))
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems("dto.password")));

		// try with an existing contactInformation
		dto = contactInformationChangeDto();
		dto.setNewContactInformation(SECOND_USER_CONTACT_INFORMATION);;
		mvc.perform(testTemplate.requestContactInformationChange(getUser().getId(),token,dto))
				.andExpect(status().is(400));
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems("dto.newContactInformation")));
		
		verify(aopUnproxy(mailSender), never()).send(any());
	}
}
