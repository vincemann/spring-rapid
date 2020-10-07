package com.github.vincemann.springlemon.demo;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.demo.domain.MySignupForm;
import com.github.vincemann.springrapid.core.util.MapperUtils;
import com.github.vincemann.springlemon.auth.domain.LemonRoles;
import com.github.vincemann.springlemon.auth.util.LemonValidationUtils;
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

public class SignupMvcTests extends AbstractMvcTests {
	
	@Test
	public void testSignupWithInvalidData() throws Exception {
		
		MySignupForm signupForm = new MySignupForm("abc", "user1", null);

		mvc.perform(post("/api/core/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(MapperUtils.toJson(signupForm)))
				.andExpect(status().is(422))
//				.andExpect(jsonPath("$.errors[*].field").value(hasSize(3)))
				.andExpect(jsonPath("$.errors[*].field").value(hasItems(
					"user.email", "user.password"/*, "user.name"*/)))
				.andExpect(jsonPath("$.errors[*].code").value(hasItems(
						"{com.naturalprogrammer.spring.invalid.email}",
						/*"{blank.name}",*/
						"{com.naturalprogrammer.spring.invalid.email.size}",
						"{com.naturalprogrammer.spring.invalid.password.size}")))
				.andExpect(jsonPath("$.errors[*].message").value(hasItems(
						"Not a well formed email address",
						/*"Name required",*/
						"Email must be between 4 and 250 characters",
						"Password must be between 6 and 50 characters")));
		
		verify(mailSender, never()).send(any());
	}

	@Test
	public void testSignup() throws Exception {
		
		MySignupForm signupForm = new MySignupForm("user.foo@example.com", "user123", "User Foo");

		mvc.perform(post("/api/core/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(MapperUtils.toJson(signupForm)))
				.andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.email").value("user.foo@example.com"))
				.andExpect(jsonPath("$.roles").value(hasSize(2)))
				.andExpect(jsonPath("$.roles").value(hasItems(LemonRoles.UNVERIFIED,LemonRoles.USER)))
//				.andExpect(jsonPath("$.tag.name").value("User Foo"))
				.andExpect(jsonPath("$.unverified").value(true))
				.andExpect(jsonPath("$.blocked").value(false))
				.andExpect(jsonPath("$.admin").value(false))
				.andExpect(jsonPath("$.goodUser").value(false));
//				.andExpect(jsonPath("$.goodAdmin").value(false));
				
		verify(mailSender).send(any());

		// Ensure that password got encrypted
		Assertions.assertNotEquals("user123", ((AbstractUser) userRepository.findByEmail("user.foo@example.com").get()).getPassword());
	}
	
	@Test
	public void testSignupDuplicateEmail() throws Exception {

		MySignupForm signupForm = new MySignupForm("user@example.com", "user123", "User");
		mvc.perform(post("/api/core/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(MapperUtils.toJson(signupForm)))
				.andExpect(status().is(422));
		
		verify(mailSender, never()).send(any());
	}
}
