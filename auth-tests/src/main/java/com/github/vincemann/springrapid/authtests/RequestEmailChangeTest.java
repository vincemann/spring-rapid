package com.github.vincemann.springrapid.authtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.RequestEmailChangeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;

public class RequestEmailChangeTest extends AbstractRapidAuthIntegrationTest {


	protected RequestEmailChangeDto emailChangeDto() {

		RequestEmailChangeDto changeForm = new RequestEmailChangeDto();
//		changeForm.setPassword(USER_PASSWORD);
		changeForm.setNewEmail(NEW_EMAIL);
		return changeForm;
	}

	@Test
	public void unverifiedUserCanRequestEmailChange() throws Exception {
		String token = login2xx(UNVERIFIED_USER_EMAIL,UNVERIFIED_USER_PASSWORD);
		testTemplate.requestEmailChange(getUnverifiedUser().getId(),token,emailChangeDto())
				.andExpect(status().is(204));

		verify(unproxy(mailSender)).send(any());

		AbstractUser<Long> updatedUser = getUserService().findById(getUnverifiedUser().getId()).get();
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getNewEmail());
		Assertions.assertEquals(UNVERIFIED_USER_EMAIL, updatedUser.getEmail());
	}

	@Test
	public void userCanRequestEmailChange() throws Exception {
		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		testTemplate.requestEmailChange(getUser().getId(),token,emailChangeDto())
				.andExpect(status().is(204));

		verify(unproxy(mailSender)).send(any());

		AbstractUser<Long> updatedUser = getUserService().findById(getUser().getId()).get();
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getNewEmail());
		Assertions.assertEquals(USER_EMAIL, updatedUser.getEmail());
	}

	/**
     * A admin should be able to request changing email of another user.
     */
	@Test
	public void adminCanRequestEmailChangeOfDiffUser() throws Exception {
		String token = login2xx(ADMIN_EMAIL,ADMIN_PASSWORD);
		testTemplate.requestEmailChange(getUser().getId(),token,emailChangeDto())
				.andExpect(status().is(204));

		AbstractUser<Long> updatedUser = getUserService().findById(getUser().getId()).get();
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getNewEmail());
	}	
	
	/**
     * A request changing email of unknown user.
     */
	@Test
	public void cantRequestEmailChangeOfUnknownUser() throws Exception {
		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		testTemplate.requestEmailChange(UNKNOWN_USER_ID,token,emailChangeDto())
				.andExpect(status().is(404));
		
		verify(unproxy(mailSender), never()).send(any());
	}

	@Test
	public void userCantRequestEmailChangeOfDiffUser() throws Exception {
		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		testTemplate.requestEmailChange(getSecondUser().getId(),token,emailChangeDto())
				.andExpect(status().is(403));
		
		verify(unproxy(mailSender), never()).send(any());

		AbstractUser<Long> updatedUser = getUserService().findById(getSecondUser().getId()).get();
		Assertions.assertNull(updatedUser.getNewEmail());
	}
	

	@Test
	public void adminCantRequestEmailChangeOfDiffAdmin() throws Exception {
		//unverified admins are not treated differently than verified admins
		String token = login2xx(ADMIN_EMAIL,ADMIN_PASSWORD);
		testTemplate.requestEmailChange(getSecondAdmin().getId(),token,emailChangeDto())
				.andExpect(status().is(403));
		
		verify(unproxy(mailSender), never()).send(any());

		AbstractUser<Long> updatedUser = getUserService().findById(getSecondAdmin().getId()).get();
		Assertions.assertNull(updatedUser.getNewEmail());
	}

	/**
     * Trying with invalid data.
	 * @throws Exception 
	 * @throws JsonProcessingException 
     */
	@Test
	public void cantRequestEmailChangeWithInvalidData() throws JsonProcessingException, Exception {
		RequestEmailChangeDto dto = new RequestEmailChangeDto();
		dto.setNewEmail(null);
//		dto.setPassword(null);
		// try with null newEmail
		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		testTemplate.requestEmailChange(getUser().getId(),token,dto)
				.andExpect(status().is(400));
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
//						"dto.newEmail"
						/*"dto.password"*/
    	
		dto = new RequestEmailChangeDto();
//		dto.setPassword("");
		dto.setNewEmail("");
		
    	// try with blank newEmail
		testTemplate.requestEmailChange(getUser().getId(),token,dto)
				.andExpect(status().is(400));
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(2)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
//						"dto.newEmail"
//						/*"dto.password"*/)));

		// try with invalid newEmail
		dto = new RequestEmailChangeDto();
		dto.setNewEmail(INVALID_EMAIL);
		testTemplate.requestEmailChange(getUser().getId(),token,dto)
				.andExpect(status().is(400));
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems("dto.newEmail")));

		// try with wrong password
//		dto = dto();
//		dto.setPassword("wrong-password");
//		mvc.perform(post("/api/core/users/{id}/email-change-request", getUnverifiedUser().getId())
//				.contentType(MediaType.APPLICATION_JSON)
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
//				.content(MapperUtils.toJson(dto)))
//				.andExpect(status().is(400))
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems("updatedUser.password")));

		// try with null password
//		dto = dto();
//		dto.setPassword(null);
//		mvc.perform(post("/api/core/users/{id}/email-change-request", getUnverifiedUser().getId())
//				.contentType(MediaType.APPLICATION_JSON)
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
//				.content(MapperUtils.toJson(dto)))
//				.andExpect(status().is(400))
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems("dto.password")));

		// try with an existing email
		dto = emailChangeDto();
		dto.setNewEmail(SECOND_USER_EMAIL);;
		testTemplate.requestEmailChange(getUser().getId(),token,dto)
				.andExpect(status().is(400));
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(1)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems("dto.newEmail")));
		
		verify(unproxy(mailSender), never()).send(any());
	}
}
