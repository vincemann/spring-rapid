package com.github.vincemann.springrapid.authtests;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FetchUserTest extends AbstractRapidAuthIntegrationTest {
	


	@Test
	public void anonKnowsEmail_canFindMatchingId() throws Exception {
		//todo create lemonDtoMappingContext with lemon endpoints like parentAware and create dto that only has id that maps for find,response,foreign,noRoles
		
		mvc.perform(post(authProperties.getController().getFetchByEmailUrl())
                .param("email", ADMIN_EMAIL)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
				.andExpect(jsonPath("$.id").value(getAdmin().getId()))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.credentialsUpdatedAt").doesNotExist());
//				.andExpect(jsonPath("$.name").value("Admin 1"));
	}

	@Test
	public void anonKnowsInvalidEmail_cantFindMatchingId() throws Exception {
		
		// email does not exist
		mvc.perform(post(authProperties.getController().getFetchByEmailUrl())
                .param("email", UNKNOWN_EMAIL)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(404));

		// Blank email
		mvc.perform(post(authProperties.getController().getFetchByEmailUrl())
                .param("email", "")
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(400));

		// Invalid email
		mvc.perform(post(authProperties.getController().getFetchByEmailUrl())
                .param("email", INVALID_EMAIL)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(404));
	}


	//	@Test
//	@Disabled //replaced by rapid controller find by Id
//	public void testUnauthenticated_fetchUserById_shouldOnlyHaveNamePayLoad() throws Exception {
//
//		mvc.perform(get("/api/core/users/{id}", getAdmin().getId()))
//                .andExpect(status().is(200))
//				.andExpect(jsonPath("$.id").value(getAdmin().getId()))
//				.andExpect(jsonPath("$.email").doesNotExist())
//				.andExpect(jsonPath("$.password").doesNotExist())
//				.andExpect(jsonPath("$.credentialsUpdatedAt").doesNotExist());
////				.andExpect(jsonPath("$.name").value("Admin 1"));
//	}
//
//	@Disabled //replaced by rapid controller find by Id
//	public void testFetchUserByIdLoggedIn() throws Exception {
//		//can see email = can see everything relevant
//		// Same user logged in -> can also see its email but that's it
//		mvc.perform(get("/api/core/users/{id}", getAdmin().getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
//                .andExpect(status().is(200))
//				.andExpect(jsonPath("$.id").value(getAdmin().getId()))
//				.andExpect(jsonPath("$.email").value(ADMIN_EMAIL))
//				.andExpect(jsonPath("$.password").doesNotExist())
//				.andExpect(jsonPath("$.credentialsUpdatedAt").doesNotExist());
////				.andExpect(jsonPath("$.name").value("Admin 1"));
//
//		// Another user logged in, can see the same as unauthenticated
//		mvc.perform(get("/api/core/users/{id}", getAdmin().getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId())))
//                .andExpect(status().is(200))
//				.andExpect(jsonPath("$.id").value(getAdmin().getId()))
//				.andExpect(jsonPath("$.email").doesNotExist());
//
//		// Admin user logged in - fetching another user can see email
//		MvcResult mvcResult = mvc.perform(get("/api/core/users/{id}", getUnverifiedUser().getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
//				.andExpect(status().is(200))
//				.andExpect(jsonPath("$.id").value(getUnverifiedUser().getId()))
//				.andExpect(jsonPath("$.email").value(UNVERIFIED_USER_EMAIL))
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
