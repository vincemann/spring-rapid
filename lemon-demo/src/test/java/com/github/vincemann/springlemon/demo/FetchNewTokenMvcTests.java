package com.github.vincemann.springlemon.demo;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.service.token.JwtService;
import com.github.vincemann.springrapid.core.util.MapperUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class FetchNewTokenMvcTests extends AbstractMvcTests {

	@SpyBean
	private LemonProperties properties;
	
	public static class Response {
		
		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}		
	}

	//todo: expiration millis einfach in mocked lemonProperties ändern für den jeweiligen test

	@Test
	public void testFetchNewToken() throws Exception {
		
		MvcResult result = mvc.perform(post("/api/core/fetch-new-auth-token")
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
				.andExpect(jsonPath("$.token").value(containsString(".")))
				.andReturn();

		Response response = MapperUtils.fromJson(result.getResponse().getContentAsString(), Response.class);
		ensureTokenWorks(response.getToken());
	}
	
	@Test
	public void testFetchNewToken_waitForExpire_shouldNotBeUsableAfter() throws Exception {
		//mock expire time
		long oldExpireTime = properties.getJwt().getExpirationMillis();
		long mockedExpireTime = 1000L;
		Mockito.doReturn(mockedExpireTime).when(properties.getJwt().getExpirationMillis());

		MvcResult result = mvc.perform(post("/api/core/fetch-new-auth-token")
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
//		        .param("expirationMillis", "1000")
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
				.andReturn();

		Response response = MapperUtils.fromJson(result.getResponse().getContentAsString(), Response.class);
		ensureTokenWorks(response.getToken());

		Thread.sleep(mockedExpireTime+1L);
		//token is now expired
		mvc.perform(get("/api/core/context")
				.header(HttpHeaders.AUTHORIZATION,
						JwtService.TOKEN_PREFIX + response.getToken()))
				.andExpect(status().is(401));

		//reset expire time
		Mockito.reset(properties);
	}

	@Test
	public void testFetchNewTokenByAdminForAnotherUser() throws Exception {
		
		MvcResult result = mvc.perform(post("/api/core/fetch-new-auth-token")
				.header(HttpHeaders.AUTHORIZATION, tokens.get(admin.getId()))
		        .param("email", UNVERIFIED_USER_EMAIL)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
				.andReturn();

		Response response = MapperUtils.fromJson(result.getResponse().getContentAsString(), Response.class);
		ensureTokenWorks(response.getToken());
	}
	
	@Test
	public void testFetchNewTokenByNonAdminForAnotherUser_shouldFail() throws Exception {
		
		mvc.perform(post("/api/core/fetch-new-auth-token")
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId()))
		        .param("email", ADMIN_EMAIL)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(403));
	}
}
