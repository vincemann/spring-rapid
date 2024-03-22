package com.github.vincemann.springrapid.authtests.tests;

import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.service.ContactInformationServiceImpl;
import com.github.vincemann.springrapid.auth.util.JwtUtils;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChangeContactInformationTest extends AuthIntegrationTest {


    @Test
    public void userCanChangeOwnContactInformation() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        String token = userController.login2xx(USER_CONTACT_INFORMATION,USER_PASSWORD);
        RequestContactInformationChangeDto dto = new RequestContactInformationChangeDto(user.getContactInformation(), NEW_CONTACT_INFORMATION);
        userController.requestContactInformationChange2xx(dto,token);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());

        mvc.perform(userController.changeContactInformationWithLink(msg.getLink(), token))
                //gets new token for new contactInformation to use
                .andExpect(status().is(204))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                .andExpect(content().string(""));


        AbstractUser<?> updatedUser = testAdapter.fetchUser(NEW_CONTACT_INFORMATION);
        Assertions.assertNull(updatedUser.getNewContactInformation());
        Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getContactInformation());
    }

    @Test
    public void unverifiedUserCanChangeOwnContactInformation() throws Exception {
        AbstractUser<?> user = testAdapter.createUnverifiedUser();
        String token = userController.login2xx(UNVERIFIED_USER_CONTACT_INFORMATION, UNVERIFIED_USER_PASSWORD);
        RequestContactInformationChangeDto dto = new RequestContactInformationChangeDto(user.getContactInformation(), NEW_CONTACT_INFORMATION);
        userController.requestContactInformationChange2xx(dto,token);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());

        mvc.perform(userController.changeContactInformationWithLink(msg.getLink(), token))
                //gets new token for new contactInformation to use
                .andExpect(status().is(204))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                .andExpect(content().string(""));


        AbstractUser<?> updatedUser = testAdapter.fetchUser(NEW_CONTACT_INFORMATION);
        Assertions.assertNull(updatedUser.getNewContactInformation());
        Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getContactInformation());
    }

    @Test
    public void userCantChangeContactInformationOfDiffUser() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        AbstractUser<?> secondUser = testAdapter.createSecondUser();

        String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        RequestContactInformationChangeDto dto = new RequestContactInformationChangeDto(user.getContactInformation(), NEW_CONTACT_INFORMATION);
        userController.requestContactInformationChange2xx(dto,token);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());

        token = userController.login2xx(SECOND_USER_CONTACT_INFORMATION, SECOND_USER_PASSWORD);
        // other user has sniffed correct code, but wrong token
        mvc.perform(userController.changeContactInformationWithLink(msg.getLink(), token))
                //gets new token for new contactInformation to use
                .andExpect(status().isForbidden());
    }

    @Test
    public void cantChangeOwnContactInformationWithSameCodeTwice() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        RequestContactInformationChangeDto dto = new RequestContactInformationChangeDto(user.getContactInformation(), NEW_CONTACT_INFORMATION);
        userController.requestContactInformationChange2xx(dto,token);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());

        mvc.perform(userController.changeContactInformationWithLink(msg.getLink(), token))
                //gets new token for new contactInformation to use
                .andExpect(status().is2xxSuccessful())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                .andExpect(content().string(""));

        mvc.perform(userController.changeContactInformationWithLink(msg.getLink(), token))
                //gets new token for new contactInformation to use
                .andExpect(status().is(401))
                .andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION));
    }


    /**
     * Providing a wrong changeContactInformationCode shouldn't work.
     */
    @Test
    public void cantChangeOwnContactInformationWithInvalidCode() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        AbstractUser<?> secondUser = testAdapter.createSecondUser();
        String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        RequestContactInformationChangeDto dto = new RequestContactInformationChangeDto(user.getContactInformation(), NEW_CONTACT_INFORMATION);
        userController.requestContactInformationChange2xx(dto,token);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());


        // Blank token
        String code = "";
        mvc.perform(userController.changeContactInformation(code, token))
                //gets new token for new contactInformation to use
                .andExpect(status().isUnauthorized());

        // Wrong audience
        code = modifyCode(msg.getCode(), "", null, null, null, null);
        mvc.perform(userController.changeContactInformation(code, token))
                //gets new token for new contactInformation to use
                .andExpect(status().isForbidden());


        // Wrong userId subject
        code = modifyCode(msg.getCode(), null, secondUser.getId().toString(), null, null, null);
        mvc.perform(userController.changeContactInformation(code, token))
                //gets new token for new contactInformation to use
                .andExpect(status().isForbidden());

        // Wrong new contactInformation
        code = modifyCode(msg.getCode(), null, null, null, null, MapUtils.mapOf("newContactInformation", "wrong.new.contactInformation@example.com"));
        mvc.perform(userController.changeContactInformation(code, token))
                //gets new token for new contactInformation to use
                .andExpect(status().isBadRequest());
    }


    /**
     * Providing an obsolete changeContactInformationCode shouldn't work.
     */
    @Test
    public void cantChangeOwnContactInformationWithObsoleteCode() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        RequestContactInformationChangeDto dto = new RequestContactInformationChangeDto(user.getContactInformation(), NEW_CONTACT_INFORMATION);
        userController.requestContactInformationChange2xx(dto,token);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());
        // credentials updated after the request for contactInformation change was made

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            AbstractUser update = testAdapter.fetchUser(user.getContactInformation());
            update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        });


        // A new auth token is needed, because old one would be obsolete!
        token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);


        // now ready to test!
        mvc.perform(userController.changeContactInformationWithLink(msg.getLink(), token))
                //gets new token for new contactInformation to use
                .andExpect(status().is(403));
    }


	/**
     * Trying without having requested first.
	 * @throws Exception
     */
    @Test
    @Disabled // you can never get the real code without requesting contactInformation change first
    public void cantChangeOwnContactInformationWithoutRequestingContactInformationChangeFirst() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        String code = createChangeContactInformationToken(user, NEW_CONTACT_INFORMATION, 600000L);
        String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        mvc.perform(userController.changeContactInformation(code, token))
                //gets new token for new contactInformation to use
                .andExpect(status().isForbidden());
    }


    /**
     * Trying after some user registers the newContactInformation, leaving it non unique.
     * @throws Exception
     */
    @Test
    public void cantChangeOwnContactInformationWhenNewContactInformationNotUnique() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        AbstractUser<?> secondUser = testAdapter.createSecondUser();
        String token = userController.login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        RequestContactInformationChangeDto dto = new RequestContactInformationChangeDto(user.getContactInformation(), NEW_CONTACT_INFORMATION);
        userController.requestContactInformationChange2xx(dto,token);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());

        // Some other user changed to the same contactInformation, before i could issue my request

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            AbstractUser<?> update = testAdapter.fetchUser(secondUser.getContactInformation());
            update.setContactInformation(NEW_CONTACT_INFORMATION);
        });

        mvc.perform(userController.changeContactInformationWithLink(msg.getLink(), token))
                // gets new token for new contactInformation to use
                .andExpect(status().is(400));
    }

    protected String createChangeContactInformationToken(AbstractUser targetUser, String newContactInformation, Long expiration) {
        return jweTokenService.createToken(
                JwtUtils.create(
                        ContactInformationServiceImpl.CHANGE_CONTACT_INFORMATION_AUDIENCE,
                        targetUser.getId().toString(),
                        expiration,
                        MapUtils.mapOf("newContactInformation", newContactInformation)));
    }

}
