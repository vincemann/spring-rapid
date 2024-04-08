package com.github.vincemann.springrapid.authtests.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;

import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FetchNewTokenTest extends AuthIntegrationTest {


	@Test
	public void userCanFetchNewTokenForOwnUser() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		String token = userController.login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		String newToken = userController.fetchNewToken2xx(token);
		assertTokenWorks(newToken,user.getId());
	}
	
	@Test
	public void givenFetchedNewTokenAndItExpired_whenTestToken_thenIsInvalid() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		//mock expire time
		// use 1 sec here to avoid token already beeing expired when calling ensure token works
		long mockedExpireTime = 1000L;
		Mockito.doReturn(mockedExpireTime).when(jwt).getExpirationMillis();

		String token = userController.login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		userController.fetchNewToken2xx(token);

		Thread.sleep(mockedExpireTime+1L);
		assertTokenDoesNotWork(token);

	}

	@Test
	public void adminCanFetchNewTokenForDiffUser() throws Exception {
		AbstractUser<?> user = testAdapter.createUser();
		AbstractUser<?> admin = testAdapter.createAdmin();
		String token = userController.login2xx(ADMIN_CONTACT_INFORMATION,ADMIN_PASSWORD);
		String newToken = userController.fetchNewToken2xx(token);
		assertTokenWorks(newToken,user.getId());
	}


}
