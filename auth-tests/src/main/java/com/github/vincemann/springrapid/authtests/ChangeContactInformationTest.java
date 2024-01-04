package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.core.util.TransactionalTemplate;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;

import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChangeContactInformationTest extends AbstractRapidAuthIntegrationTest {


	@Autowired
    TransactionalTemplate transactionalTemplate;

	//works solo but token is obsolete when run in group
//	@Disabled
	@Test
	public void canChangeOwnContactInformation() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		MailData mailData = testTemplate.requestContactInformationChange2xx(getUser().getId(), token,
				new RequestContactInformationChangeDto(NEW_CONTACT_INFORMATION));

		mvc.perform(testTemplate.changeContactInformationWithLink(mailData.getLink(),token))
				//gets new token for new contactInformation to use
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(getUser().getId()));

		
		AbstractUser<Serializable> updatedUser = getUserService().findById(getUser().getId()).get();
		Assertions.assertNull(updatedUser.getNewContactInformation());
		Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getContactInformation());
	}

	@Test
	public void unverifiedUserCanChangeOwnContactInformation() throws Exception {
		String token = login2xx(UNVERIFIED_USER_CONTACT_INFORMATION,UNVERIFIED_USER_PASSWORD);
		MailData mailData = testTemplate.requestContactInformationChange2xx(getUnverifiedUser().getId(), token,
				new RequestContactInformationChangeDto(NEW_CONTACT_INFORMATION));

		mvc.perform(testTemplate.changeContactInformationWithLink(mailData.getLink(),token))
				//gets new token for new contactInformation to use
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
				.andExpect(jsonPath("$.id").value(getUnverifiedUser().getId()));


		AbstractUser<Serializable> updatedUser = getUserService().findById(getUnverifiedUser().getId()).get();
		Assertions.assertNull(updatedUser.getNewContactInformation());
		Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getContactInformation());
	}

	@Test
	public void cantChangeContactInformationOfDiffUser() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		MailData mailData = testTemplate.requestContactInformationChange2xx(getUser().getId(), token,
				new RequestContactInformationChangeDto(NEW_CONTACT_INFORMATION));

		token = login2xx(SECOND_USER_CONTACT_INFORMATION,SECOND_USER_PASSWORD);
		// other user has sniffed correct code, but wrong token
		mvc.perform(testTemplate.changeContactInformationWithLink(mailData.getLink(),token))
				//gets new token for new contactInformation to use
				.andExpect(status().isForbidden());
	}

	@Test
	public void cantChangeOwnContactInformationWithSameCodeTwice() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		MailData mailData = testTemplate.requestContactInformationChange2xx(getUser().getId(), token,
				new RequestContactInformationChangeDto(NEW_CONTACT_INFORMATION));

		mvc.perform(testTemplate.changeContactInformationWithLink(mailData.getLink(),token))
				//gets new token for new contactInformation to use
				.andExpect(status().is2xxSuccessful());

		mvc.perform(testTemplate.changeContactInformationWithLink(mailData.getLink(),token))
				//gets new token for new contactInformation to use
				.andExpect(status().is(401))
				.andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION))
				.andExpect(jsonPath("$.id").doesNotExist());
	}


    /**
     * Providing a wrong changeContactInformationCode shouldn't work.
     */
	@Test
	public void cantChangeOwnContactInformationWithInvalidCode() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		MailData mailData = testTemplate.requestContactInformationChange2xx(getUser().getId(), token,
				new RequestContactInformationChangeDto(NEW_CONTACT_INFORMATION));



		// Blank token
		String code = "";
		mvc.perform(testTemplate.changeContactInformation(code,token))
				//gets new token for new contactInformation to use
				.andExpect(status().is(400));

		// Wrong audience
		code = modCode(mailData.getCode(),"",null,null,null,null);
		mvc.perform(testTemplate.changeContactInformation(code,token))
				//gets new token for new contactInformation to use
				.andExpect(status().is(403));


		// Wrong userId subject
		code = modCode(mailData.getCode(),null,getSecondUser().getId().toString(),null,null,null);
		mvc.perform(testTemplate.changeContactInformation(code,token))
				//gets new token for new contactInformation to use
				.andExpect(status().is(403));

		// Wrong new contactInformation
		code = modCode(mailData.getCode(),null,null,null,null,MapUtils.mapOf("newContactInformation", "wrong.new.contactInformation@example.com"));
		mvc.perform(testTemplate.changeContactInformation(code,token))
				//gets new token for new contactInformation to use
				.andExpect(status().is(403));
	}

//
//    /**
//     * Providing an obsolete changeContactInformationCode shouldn't work.
//     */
//    //todo sometimes 401 sometimes 403
	@Test
	public void cantChangeOwnContactInformationWithObsoleteCode() throws Exception {
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		MailData mailData = testTemplate.requestContactInformationChange2xx(getUser().getId(), token,
				new RequestContactInformationChangeDto(NEW_CONTACT_INFORMATION));
		// credentials updated after the request for contactInformation change was made
		transactionalTemplate.doInTransaction(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				AbstractUser<Serializable> user = getUserService().findById(getUser().getId()).get();
				user.setCredentialsUpdatedMillis(System.currentTimeMillis());
				getUserService().fullUpdate(user);
			}
		});

		// A new auth token is needed, because old one would be obsolete!
		token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);


		// now ready to test!
		mvc.perform(testTemplate.changeContactInformationWithLink(mailData.getLink(),token))
				//gets new token for new contactInformation to use
				.andExpect(status().is(403));
	}
//
//	/**
//     * Trying without having requested first.
//	 * @throws Exception
//     */
	@Test
	@Disabled // you can never get the real code without requesting contactInformation change first
	public void cantChangeOwnContactInformationWithoutRequestingContactInformationChangeFirst() throws Exception {
		String code = createChangeContactInformationToken(getUser(), NEW_CONTACT_INFORMATION, 600000L);
		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		mvc.perform(testTemplate.changeContactInformation(code,token))
				//gets new token for new contactInformation to use
				.andExpect(status().isForbidden());
	}
//
//    /**
//     * Trying after some user registers the newContactInformation, leaving it non unique.
//     * @throws Exception
//     */
	@Test
	public void cantChangeOwnContactInformationWhenNewContactInformationNotUnique() throws Exception {

		String token = login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
		MailData mailData = testTemplate.requestContactInformationChange2xx(getUser().getId(), token,
				new RequestContactInformationChangeDto(NEW_CONTACT_INFORMATION));

		// Some other user changed to the same contactInformation, before i could issue my request
		transactionalTemplate.doInTransaction(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				AbstractUser<Serializable> user = getUserService().findById(getSecondUser().getId()).get();
				user.setContactInformation(NEW_CONTACT_INFORMATION);
				getUserService().fullUpdate(user);
			}
		});


		mvc.perform(testTemplate.changeContactInformationWithLink(mailData.getLink(),token))
				//gets new token for new contactInformation to use
				.andExpect(status().is(400));
	}

	protected String createChangeContactInformationToken(AbstractUser targetUser, String newContactInformation, Long expiration){
		return jweTokenService.createToken(
				RapidJwt.create(
						AbstractUserService.CHANGE_CONTACT_INFORMATION_AUDIENCE,
						targetUser.getId().toString(),
						expiration,
						MapUtils.mapOf("newContactInformation", newContactInformation)));
	}

}
