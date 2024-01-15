package com.github.vincemann.springrapid.authtests;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;

import com.github.vincemann.springrapid.authtest.controller.template.AbstractUserControllerTestTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MvcResult;

public class FetchNewTokenTest extends RapidAuthIntegrationTest {


	@Test
	public void canFetchNewTokenForOwnUser() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		MvcResult result = mvc.perform(userController.fetchNewToken(token))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.token").value(containsString(".")))
				.andReturn();


		AbstractUserControllerTestTemplate.ResponseToken response =deserialize(result.getResponse().getContentAsString(), AbstractUserControllerTestTemplate.ResponseToken.class);
		ensureTokenWorks(response.getToken(),getUser().getId());
	}
	
	@Test
	public void fetchNewToken_waitForExpire_cantLoginWithIt() throws Exception {
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
		MvcResult result = mvc.perform(userController.fetchNewToken(token, USER_CONTACT_INFORMATION))
				.andExpect(status().is2xxSuccessful())
				.andReturn();


		AbstractUserControllerTestTemplate.ResponseToken response =deserialize(result.getResponse().getContentAsString(), AbstractUserControllerTestTemplate.ResponseToken.class);
		ensureTokenWorks(response.getToken(),getUser().getId());
	}
	
	@Test
	public void cantFetchTokenForDiffUser() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		mvc.perform(userController.fetchNewToken(token,SECOND_USER_CONTACT_INFORMATION))
				.andExpect(status().isForbidden());
	}

}
