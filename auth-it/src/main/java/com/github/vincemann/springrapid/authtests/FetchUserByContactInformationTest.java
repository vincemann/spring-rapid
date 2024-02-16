package com.github.vincemann.springrapid.authtests;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;


public class FetchUserByContactInformationTest extends RapidAuthIntegrationTest {
	


	@Test
	public void givenAnonKnowsForeignUsersCI_whenFindByCI_thenRetrievesIdOnly() throws Exception {
		mvc.perform(userController.findByContactInformation(USER_CONTACT_INFORMATION))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.id").value(getUser().getId()))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.credentialsUpdatedAt").doesNotExist());
	}

	@Test
	public void anonKnowsInvalidContactInformation_cantFindMatchingId() throws Exception {
		
		// contactInformation does not exist
		mvc.perform(userController.findByContactInformation(UNKNOWN_CONTACT_INFORMATION))
                .andExpect(status().isNotFound());

		// Blank contactInformation
		mvc.perform(userController.findByContactInformation(""))
				.andExpect(status().isBadRequest());
	}


	//	@Test
//	@Disabled //replaced by rapid controller find by Id
//	public void testUnauthenticated_fetchUserById_shouldOnlyHaveNamePayLoad() throws Exception {
//
//		mvc.perform(get("/api/core/users/{id}", getAdmin().getId()))
//                .andExpect(status().is(200))
//				.andExpect(jsonPath("$.id").value(getAdmin().getId()))
//				.andExpect(jsonPath("$.contactInformation").doesNotExist())
//				.andExpect(jsonPath("$.password").doesNotExist())
//				.andExpect(jsonPath("$.credentialsUpdatedAt").doesNotExist());
////				.andExpect(jsonPath("$.name").value("Admin 1"));
//	}
//
//	@Disabled //replaced by rapid controller find by Id
//	public void testFetchUserByIdLoggedIn() throws Exception {
//		//can see contactInformation = can see everything relevant
//		// Same user logged in -> can also see its contactInformation but that's it
//		mvc.perform(get("/api/core/users/{id}", getAdmin().getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
//                .andExpect(status().is(200))
//				.andExpect(jsonPath("$.id").value(getAdmin().getId()))
//				.andExpect(jsonPath("$.contactInformation").value(ADMIN_CONTACT_INFORMATION))
//				.andExpect(jsonPath("$.password").doesNotExist())
//				.andExpect(jsonPath("$.credentialsUpdatedAt").doesNotExist());
////				.andExpect(jsonPath("$.name").value("Admin 1"));
//
//		// Another user logged in, can see the same as unauthenticated
//		mvc.perform(get("/api/core/users/{id}", getAdmin().getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId())))
//                .andExpect(status().is(200))
//				.andExpect(jsonPath("$.id").value(getAdmin().getId()))
//				.andExpect(jsonPath("$.contactInformation").doesNotExist());
//
//		// Admin user logged in - fetching another user can see contactInformation
//		MvcResult mvcResult = mvc.perform(get("/api/core/users/{id}", getUnverifiedUser().getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
//				.andExpect(status().is(200))
//				.andExpect(jsonPath("$.id").value(getUnverifiedUser().getId()))
//				.andExpect(jsonPath("$.contactInformation").value(UNVERIFIED_USER_CONTACT_INFORMATION))
//				.andReturn();
//
//	}
//
//	@Test
//	@Disabled //replaced by rapid controller find by Id
//	public void testFetchNonExistingUserById_shouldNotFound() throws Exception {
//
//		mvc.perform(get("/api/core/users/99"))
//                .andExpect(status().is(404));
//	}
}
