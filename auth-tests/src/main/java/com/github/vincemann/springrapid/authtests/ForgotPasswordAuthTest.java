package com.github.vincemann.springrapid.authtests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class ForgotPasswordAuthTest extends AbstractRapidAuthTest {


	// todo bs anon should issue this request
	@Test
	public void anonCanIssueForgotPasswordForUser() throws Exception {
		
		mvc.perform(post(authProperties.getController().getForgotPasswordUrl())
                .param("email", ADMIN_EMAIL)
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(204));

		verify(unproxy(mailSender)).send(any());
	}


	@Test
	public void loggedIn_canIssueForgotPasswordForOwnUser() throws Exception {

		mvc.perform(post(authProperties.getController().getForgotPasswordUrl())
				.param("email", ADMIN_EMAIL)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId()))
				.header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().is(204));

		verify(unproxy(mailSender)).send(any());
	}
	
	@Test
	public void cantIssueForgotPasswordForInvalidEmail() throws Exception {
		
		// Unknown email
		mvc.perform(post(authProperties.getController().getForgotPasswordUrl())
                .param("email", "unknown@example.com")
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(404));

		// Null email
		mvc.perform(post(authProperties.getController().getForgotPasswordUrl())
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(400));

		// Blank email
		mvc.perform(post(authProperties.getController().getForgotPasswordUrl())
                .param("email", "")
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(400));
		
		// Wrong email format
		mvc.perform(post(authProperties.getController().getForgotPasswordUrl())
                .param("email", "wrong-email-format")
                .header("contentType",  MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(404));
		
		verify(unproxy(mailSender), never()).send(any());
	}
}
