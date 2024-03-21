package com.github.vincemann.springrapid.authtests.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FetchNewTokenTest extends RapidAuthIntegrationTest {


	@Test
	public void userCanFetchNewTokenForOwnUser() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		String newToken = userController.fetchNewToken2xx(token);
		assertTokenWorks(newToken,getUser().getId());
	}
	
	@Test
	public void givenFetchedNewTokenAndItExpired_whenTestToken_thenIsInvalid() throws Exception {
		//mock expire time
		// use 1 sec here to avoid token already beeing expired when calling ensure token works
		long mockedExpireTime = 1000L;
		Mockito.doReturn(mockedExpireTime).when(jwt).getExpirationMillis();

		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		userController.fetchNewToken2xx(token);

		Thread.sleep(mockedExpireTime+1L);
		assertTokenDoesNotWork(token);

	}

	@Test
	public void adminCanFetchNewTokenForDiffUser() throws Exception {
		String token = login2xx(ADMIN_CONTACT_INFORMATION,ADMIN_PASSWORD);
		String newToken = userController.fetchNewToken2xx(token);
		assertTokenWorks(newToken,getUser().getId());
	}
	
	@Test
	public void cantFetchTokenForDiffUser() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		mvc.perform(userController.fetchNewToken(token,SECOND_USER_CONTACT_INFORMATION))
				.andExpect(status().isForbidden());
	}

}
