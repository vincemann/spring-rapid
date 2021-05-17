package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.RequestEmailChangeDto;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.text.ParseException;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChangeEmailTest extends AbstractRapidAuthIntegrationTest {

	@Autowired
	private JweTokenService jweTokenService;


	//works solo but token is obsolete when run in group
//	@Disabled
	@Test
	public void canChangeOwnEmail() throws Exception {
		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		MailData mailData = testTemplate.requestEmailChange2xx(getUser().getId(), token,
				new RequestEmailChangeDto(NEW_EMAIL));

		testTemplate.changeEmail(getUser().getId(),mailData.getCode(),token)
				//gets new token for new email to use
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(getUser().getId()));

		
		AbstractUser<Long> updatedUser = getUserService().findById(getUser().getId()).get();
		Assertions.assertNull(updatedUser.getNewEmail());
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getEmail());
	}

	@Test
	public void unverifiedUserCanChangeOwnEmail() throws Exception {
		String token = login2xx(UNVERIFIED_USER_EMAIL,UNVERIFIED_USER_PASSWORD);
		MailData mailData = testTemplate.requestEmailChange2xx(getUnverifiedUser().getId(), token,
				new RequestEmailChangeDto(NEW_EMAIL));

		testTemplate.changeEmail(getUnverifiedUser().getId(),mailData.getCode(),token)
				//gets new token for new email to use
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(getUnverifiedUser().getId()));


		AbstractUser<Long> updatedUser = getUserService().findById(getUnverifiedUser().getId()).get();
		Assertions.assertNull(updatedUser.getNewEmail());
		Assertions.assertEquals(NEW_EMAIL, updatedUser.getEmail());
	}

	@Test
	public void cantChangeEmailOfDiffUser() throws Exception {
		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		MailData mailData = testTemplate.requestEmailChange2xx(getUser().getId(), token,
				new RequestEmailChangeDto(NEW_EMAIL));

		token = login2xx(SECOND_USER_EMAIL,SECOND_USER_PASSWORD);
		// other user has sniffed correct code, but wrong token
		testTemplate.changeEmail(getUser().getId(),mailData.getCode(),token)
				//gets new token for new email to use
				.andExpect(status().isForbidden());
	}

	@Test
	public void cantChangeOwnEmailWithSameCodeTwice() throws Exception {
		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		MailData mailData = testTemplate.requestEmailChange2xx(getUser().getId(), token,
				new RequestEmailChangeDto(NEW_EMAIL));

		testTemplate.changeEmail(getUser().getId(),mailData.getCode(),token)
				//gets new token for new email to use
				.andExpect(status().is2xxSuccessful());

		testTemplate.changeEmail(getUser().getId(),mailData.getCode(),token)
				//gets new token for new email to use
				.andExpect(status().is(401))
				.andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION))
				.andExpect(jsonPath("$.id").doesNotExist());
	}


    /**
     * Providing a wrong changeEmailCode shouldn't work.
     */
	@Test
	public void cantChangeOwnEmailWithInvalidCode() throws Exception {
		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		MailData mailData = testTemplate.requestEmailChange2xx(getUser().getId(), token,
				new RequestEmailChangeDto(NEW_EMAIL));



		// Blank token
		testTemplate.changeEmail(getUser().getId(),"",token)
				//gets new token for new email to use
				.andExpect(status().is(400));

		// Wrong audience
		String code = modCode(mailData.getCode(),"",null,null,null,null);
		testTemplate.changeEmail(getUser().getId(),code,token)
				//gets new token for new email to use
				.andExpect(status().is(403));


		// Wrong userId subject
		code = modCode(mailData.getCode(),null,getSecondUser().getId().toString(),null,null,null);
		testTemplate.changeEmail(getUser().getId(),code,token)
				//gets new token for new email to use
				.andExpect(status().is(403));

		// Wrong new email
		code = modCode(mailData.getCode(),null,null,null,null,MapUtils.mapOf("newEmail", "wrong.new.email@example.com"));
		testTemplate.changeEmail(getUser().getId(),code,token)
				//gets new token for new email to use
				.andExpect(status().is(403));
	}

	protected String modCode(String code, String aud, String subject, Long expirationMillis,Long issuedAt, Map<String,Object> otherClaims) throws BadTokenException, ParseException {
		JWTClaimsSet claims = jweTokenService.parseToken(code);
		claims = RapidJwt.mod(claims,aud,subject,expirationMillis,issuedAt,otherClaims);
		String moddedCode = jweTokenService.createToken(claims);
		return moddedCode;
	}
//
//    /**
//     * Providing an obsolete changeEmailCode shouldn't work.
//     */
//    //todo sometimes 401 sometimes 403
	@Test
	public void cantChangeOwnEmailWithObsoleteCode() throws Exception {
		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		MailData mailData = testTemplate.requestEmailChange2xx(getUser().getId(), token,
				new RequestEmailChangeDto(NEW_EMAIL));
		// credentials updated after the request for email change was made
//		Thread.sleep(1L);
		AbstractUser<Long> user = getUserService().findById(getUser().getId()).get();
		user.setCredentialsUpdatedMillis(System.currentTimeMillis());
		getUserService().update(user);

//		Thread.sleep(1L);

		// A new auth token is needed, because old one would be obsolete!
		token = login2xx(USER_EMAIL,USER_PASSWORD);


		// now ready to test!
		testTemplate.changeEmail(getUser().getId(),mailData.getCode(),token)
				//gets new token for new email to use
				.andExpect(status().is(403));
	}
//
//	/**
//     * Trying without having requested first.
//	 * @throws Exception
//     */
	@Test
	public void cantChangeOwnEmailWithoutRequestingEmailChangeFirst() throws Exception {
		String code = createChangeEmailToken(getUser(), NEW_EMAIL, 600000L);
		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		testTemplate.changeEmail(getUser().getId(),code,token)
				//gets new token for new email to use
				.andExpect(status().is(400));
	}
//
//    /**
//     * Trying after some user registers the newEmail, leaving it non unique.
//     * @throws Exception
//     */
	@Test
	public void cantChangeOwnEmailWhenNewEmailNotUnique() throws Exception {

		String token = login2xx(USER_EMAIL,USER_PASSWORD);
		MailData mailData = testTemplate.requestEmailChange2xx(getUser().getId(), token,
				new RequestEmailChangeDto(NEW_EMAIL));

		// Some other user changed to the same email, before i could issue my request
		AbstractUser<Long> user = getUserService().findById(getAdmin().getId()).get();
		user.setEmail(NEW_EMAIL);
		getUserService().update(user);

		testTemplate.changeEmail(getUser().getId(),mailData.getCode(),token)
				//gets new token for new email to use
				.andExpect(status().is(400));
	}

	protected String createChangeEmailToken(AbstractUser targetUser, String newEmail, Long expiration){
		return jweTokenService.createToken(
				RapidJwt.create(
						AbstractUserService.CHANGE_EMAIL_SUBJECT,
						targetUser.getId().toString(),
						expiration,
						MapUtils.mapOf("newEmail", newEmail)));
	}

}
