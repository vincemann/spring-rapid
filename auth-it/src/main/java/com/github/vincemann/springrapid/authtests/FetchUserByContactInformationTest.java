package com.github.vincemann.springrapid.authtests;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;


public class FetchUserByContactInformationTest extends RapidAuthIntegrationTest {
	


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
