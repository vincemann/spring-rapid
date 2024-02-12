package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.mail.MailData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.vincemann.springrapid.core.util.ProxyUtils.aopUnproxy;
public class SignupTest extends RapidAuthIntegrationTest {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	public void cantSignupWithInvalidData() throws Exception {
		SignupDto signupDto = createInvalidSignupDto();
		mvc.perform(userController.signup(signupDto))
				.andExpect(status().isBadRequest());

		verify(aopUnproxy(mailSender), never()).send(any());
	}

	@Test
	public void canSignup() throws Exception {
		SignupDto signupDto = createValidSignupDto();

		mvc.perform(userController.signup(signupDto))
				.andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.contactInformation").value(signupDto.getContactInformation()))
				.andExpect(jsonPath("$.roles").exists())
//				.andExpect(jsonPath("$.roles", containsInAnyOrder(roles.toArray(new String[0]))))
				.andExpect(jsonPath("$.roles", hasItem(AuthRoles.UNVERIFIED)))
				.andExpect(jsonPath("$.roles", hasItem(AuthRoles.USER)))
				.andExpect(jsonPath("$.verified").value(false))
				.andExpect(jsonPath("$.blocked").value(false))
				.andExpect(jsonPath("$.admin").value(false))
				.andExpect(jsonPath("$.goodUser").value(false));

		MailData mailData = userController.verifyMailWasSend();
		Assertions.assertEquals(signupDto.getContactInformation(), mailData.getTo());
		Assertions.assertNotNull(mailData.getCode());

		Optional<AbstractUser<Serializable>> byContactInformation = getUserService().findByContactInformation(signupDto.getContactInformation());
		Assertions.assertTrue(byContactInformation.isPresent());
		AbstractUser<Serializable> dbUser = byContactInformation.get();


		// Ensure that password got encrypted
		String savedPasswordHash = dbUser.getPassword();
		Assertions.assertTrue(passwordEncoder.matches(signupDto.getPassword(),savedPasswordHash));
	}
	
	@Test
	public void cantSignupWithDuplicateContactInformation() throws Exception {
		SignupDto signupDto = createValidSignupDto();
		userController.signup2xx(signupDto);
		signupDto.setPassword(signupDto.getPassword()+"new");

		mvc.perform(userController.signup(signupDto))
				.andExpect(status().isBadRequest());
		// mock is reset by signup2xx so never only applies to latest signup
		verify(aopUnproxy(mailSender), never()).send(any());
	}
}