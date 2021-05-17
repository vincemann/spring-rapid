package com.github.vincemann.springrapid.authtests;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.vincemann.springrapid.auth.service.token.JwtService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class FetchNewTokenTest extends AbstractRapidAuthIntegrationTest {

	
	public static class Response {
		
		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}		
	}


	@Test
	public void canFetchNewTokenForOwnUser() throws Exception {
		
		MvcResult result = mvc.perform(post(authProperties.getController().getNewAuthTokenUrl())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
				.andExpect(jsonPath("$.token").value(containsString(".")))
				.andReturn();

		Response response =deserialize(result.getResponse().getContentAsString(), Response.class);
		ensureTokenWorks(response.getToken());
	}
	
	@Test
	public void fetchNewToken_waitForExpire_cantLoginWithIt() throws Exception {
		//mock expire time
		// use 1 sec here to avoid token already beeing expired when calling ensure token works
		long mockedExpireTime = 1000L;
		Mockito.doReturn(mockedExpireTime).when(jwt).getExpirationMillis();

		MvcResult result = mvc.perform(post(authProperties.getController().getNewAuthTokenUrl())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
//		        .param("expirationMillis", "1000")
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
				.andReturn();

		Response response =deserialize(result.getResponse().getContentAsString(), Response.class);
		ensureTokenWorks(response.getToken());

		Thread.sleep(mockedExpireTime+1L);
		//token is now expired
		mvc.perform(get(authProperties.getController().getContextUrl())
				.header(HttpHeaders.AUTHORIZATION,
						JwtService.TOKEN_PREFIX + response.getToken()))
				.andExpect(status().is(401));

	}

	@Test
	public void adminCanFetchNewTokenForDiffUser() throws Exception {
		
		MvcResult result = mvc.perform(post(authProperties.getController().getNewAuthTokenUrl())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId()))
		        .param("email", UNVERIFIED_USER_EMAIL)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
				.andReturn();

		Response response =deserialize(result.getResponse().getContentAsString(), Response.class);
		ensureTokenWorks(response.getToken());
	}
	
	@Test
	public void cantFetchTokenForDiffUser() throws Exception {
		
		mvc.perform(post(authProperties.getController().getNewAuthTokenUrl())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUnverifiedUser().getId()))
		        .param("email", ADMIN_EMAIL)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(403));
	}
}
