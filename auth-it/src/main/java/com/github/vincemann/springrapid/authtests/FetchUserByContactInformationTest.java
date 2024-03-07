package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.dto.user.ReadOwnUserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;


public class FetchUserByContactInformationTest extends RapidAuthIntegrationTest {
	

	@Test
	public void userCanFindOwnUserByContactInformation() throws Exception {
		String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.findByContactInformation(USER_CONTACT_INFORMATION)
				.header(HttpHeaders.AUTHORIZATION,token))
				.andExpect(status().is2xxSuccessful())
				// only findOwnUserDto has this flag
				.andExpect(jsonPath("goodUser",equalTo(true)));
	}

	@Test
	public void userCantFindForeignUserByContactInformation() throws Exception {
		String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.findByContactInformation(SECOND_USER_CONTACT_INFORMATION)
				.header(HttpHeaders.AUTHORIZATION,token))
				.andExpect(status().isForbidden());
	}

	@Test
	public void anonCantFindUserByCI() throws Exception {
		mvc.perform(userController.findByContactInformation(USER_CONTACT_INFORMATION))
				.andExpect(status().isForbidden());

		// contactInformation does not exist
		mvc.perform(userController.findByContactInformation(UNKNOWN_CONTACT_INFORMATION))
				.andExpect(status().isNotFound());

		// Blank contactInformation
		mvc.perform(userController.findByContactInformation(""))
				.andExpect(status().isNotFound());
	}
}
