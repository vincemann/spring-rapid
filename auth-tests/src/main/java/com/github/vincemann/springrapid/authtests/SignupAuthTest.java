package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.auth.domain.dto.SignupForm;
import com.github.vincemann.springrapid.core.util.JsonUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SignupAuthTest extends AbstractRapidAuthTest {

	protected SignupForm createValidSignupForm(){
		return new SignupForm("user.foo@example.com", "userUser123");
	}


	protected SignupForm createInvalidSignupForm(){
		return new SignupForm("abc","userUser1");
	}

	@Test
	public void testSignupWithInvalidData() throws Exception {
		
		SignupForm signupForm = createInvalidSignupForm();

		mvc.perform(post(authProperties.getController().getSignupUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(signupForm)))
				.andExpect(status().is(400));
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(3)))
//				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
//					"user.email", "user.password"/*, "user.name"*/)))
//				.andExpect(jsonPath("$.errors[*].code").value(hasItems(
//						"{com.naturalprogrammer.spring.invalid.email}",
//						/*"{blank.name}",*/
//						"{com.naturalprogrammer.spring.invalid.email.size}",
//						"{com.naturalprogrammer.spring.invalid.password.size}")))
//				.andExpect(jsonPath("$.errors[*].message").value(hasItems(
//						"Not a well formed email address",
//						/*"Name required",*/
//						"Email must be between 4 and 250 characters",
//						"Password must be between 6 and 50 characters")));
		
		verify(unproxy(mailSender), never()).send(any());
	}

	@Test
	public void testSignup() throws Exception {
		
//		MySignupForm signupForm = new MySignupForm("user.foo@example.com", "user123", "User Foo");

		SignupForm signupForm = createValidSignupForm();

		mvc.perform(post(authProperties.getController().getSignupUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(signupForm)))
				.andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.email").value(signupForm.getEmail()))
				.andExpect(jsonPath("$.roles").value(hasSize(2)))
				.andExpect(jsonPath("$.roles").value(Matchers.hasItems(AuthRoles.UNVERIFIED, AuthRoles.USER)))
//				.andExpect(jsonPath("$.tag.name").value("User Foo"))
				.andExpect(jsonPath("$.unverified").value(true))
				.andExpect(jsonPath("$.blocked").value(false))
				.andExpect(jsonPath("$.admin").value(false))
				.andExpect(jsonPath("$.goodUser").value(false));
//				.andExpect(jsonPath("$.goodAdmin").value(false));
				
		verify(unproxy(mailSender)).send(any());

		// Ensure that password got encrypted
		Assertions.assertNotEquals(signupForm.getPassword(), getUserService().findByEmail(signupForm.getEmail()).get().getPassword());
	}
	
	@Test
	public void testSignupDuplicateEmail() throws Exception {

//		MySignupForm signupForm = new MySignupForm("user@example.com", "user123", "User");
		SignupForm signupForm = createValidSignupForm();
		String duplicateEmail = signupForm.getEmail();
		getUserService().save(testAdapter.createTestUser(duplicateEmail,"userUser1234", AuthRoles.USER));

		mvc.perform(post(authProperties.getController().getSignupUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(signupForm)))
				.andExpect(status().is(400));
		
		verify(unproxy(mailSender), never()).send(any());
	}
}