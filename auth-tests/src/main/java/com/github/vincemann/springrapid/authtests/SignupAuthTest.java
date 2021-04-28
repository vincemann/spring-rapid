package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.auth.domain.dto.SignupDto;
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

	protected SignupDto createValidSignupForm(){
		return new SignupDto("user.foo@example.com", "userUser123");
	}


	protected SignupDto createInvalidSignupForm(){
		return new SignupDto("abc","userUser1");
	}

	@Test
	public void cantSignupWithInvalidData() throws Exception {
		
		SignupDto signupDto = createInvalidSignupForm();

		mvc.perform(post(authProperties.getController().getSignupUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(signupDto)))
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
	public void canSignup() throws Exception {
		
//		MySignupForm signupForm = new MySignupForm("user.foo@example.com", "user123", "User Foo");

		SignupDto signupDto = createValidSignupForm();

		mvc.perform(post(authProperties.getController().getSignupUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(signupDto)))
				.andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.email").value(signupDto.getEmail()))
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
		Assertions.assertNotEquals(signupDto.getPassword(), getUserService().findByEmail(signupDto.getEmail()).get().getPassword());
	}
	
	@Test
	public void cantSignupWithDuplicateEmail() throws Exception {

//		MySignupForm signupForm = new MySignupForm("user@example.com", "user123", "User");
		SignupDto signupDto = createValidSignupForm();
		String duplicateEmail = signupDto.getEmail();
		getUserService().save(testAdapter.createTestUser(duplicateEmail,"userUser1234", AuthRoles.USER));

		mvc.perform(post(authProperties.getController().getSignupUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(signupDto)))
				.andExpect(status().is(400));
		
		verify(unproxy(mailSender), never()).send(any());
	}
}