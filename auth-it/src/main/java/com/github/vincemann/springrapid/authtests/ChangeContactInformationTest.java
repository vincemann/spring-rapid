package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.service.ContactInformationServiceImpl;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.util.JwtUtils;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.util.function.Consumer;

import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChangeContactInformationTest extends RapidAuthIntegrationTest {


    @Autowired
    TransactionTemplate transactionTemplate;


    @Test
    public void canChangeOwnContactInformation() throws Exception {
        String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        AuthMessage msg = userController.requestContactInformationChange2xx(token,
                new RequestContactInformationChangeDto(getUser().getContactInformation(), NEW_CONTACT_INFORMATION));

        mvc.perform(userController.changeContactInformationWithLink(msg.getLink(), token))
                //gets new token for new contactInformation to use
                .andExpect(status().is(204))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                .andExpect(content().string(""));


        AbstractUser<Serializable> updatedUser = getUserService().findById(getUser().getId()).get();
        Assertions.assertNull(updatedUser.getNewContactInformation());
        Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getContactInformation());
    }

    @Test
    public void unverifiedUserCanChangeOwnContactInformation() throws Exception {
        String token = login2xx(UNVERIFIED_USER_CONTACT_INFORMATION, UNVERIFIED_USER_PASSWORD);
        AuthMessage msg = userController.requestContactInformationChange2xx(token,
                new RequestContactInformationChangeDto(getUnverifiedUser().getContactInformation(), NEW_CONTACT_INFORMATION));

        mvc.perform(userController.changeContactInformationWithLink(msg.getLink(), token))
                //gets new token for new contactInformation to use
                .andExpect(status().is(204))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                .andExpect(content().string(""));


        AbstractUser<Serializable> updatedUser = getUserService().findById(getUnverifiedUser().getId()).get();
        Assertions.assertNull(updatedUser.getNewContactInformation());
        Assertions.assertEquals(NEW_CONTACT_INFORMATION, updatedUser.getContactInformation());
    }

    @Test
    public void cantChangeContactInformationOfDiffUser() throws Exception {
        String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        AuthMessage msg = userController.requestContactInformationChange2xx(token,
                new RequestContactInformationChangeDto(getUser().getContactInformation(), NEW_CONTACT_INFORMATION));

        token = login2xx(SECOND_USER_CONTACT_INFORMATION, SECOND_USER_PASSWORD);
        // other user has sniffed correct code, but wrong token
        mvc.perform(userController.changeContactInformationWithLink(msg.getLink(), token))
                //gets new token for new contactInformation to use
                .andExpect(status().isForbidden());
    }

    @Test
    public void cantChangeOwnContactInformationWithSameCodeTwice() throws Exception {
        String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        AuthMessage msg = userController.requestContactInformationChange2xx(token,
                new RequestContactInformationChangeDto(getUser().getContactInformation(), NEW_CONTACT_INFORMATION));

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
        String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        AuthMessage msg = userController.requestContactInformationChange2xx(token,
                new RequestContactInformationChangeDto(getUser().getContactInformation(), NEW_CONTACT_INFORMATION));


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
        code = modifyCode(msg.getCode(), null, getSecondUser().getId().toString(), null, null, null);
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
        String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        AuthMessage msg = userController.requestContactInformationChange2xx(token,
                new RequestContactInformationChangeDto(getUser().getContactInformation(), NEW_CONTACT_INFORMATION));
        // credentials updated after the request for contactInformation change was made

        transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {
            @SneakyThrows
            @Override
            public void accept(TransactionStatus transactionStatus) {
                AbstractUser<Serializable> user = getUserService().findById(getUser().getId()).get();
                user.setCredentialsUpdatedMillis(System.currentTimeMillis());
                getUserService().fullUpdate(user);
            }
        });

        // A new auth token is needed, because old one would be obsolete!
        token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);


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
        String code = createChangeContactInformationToken(getUser(), NEW_CONTACT_INFORMATION, 600000L);
        String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
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

        String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
        AuthMessage msg = userController.requestContactInformationChange2xx(token,
                new RequestContactInformationChangeDto(getUser().getContactInformation(), NEW_CONTACT_INFORMATION));

        // Some other user changed to the same contactInformation, before i could issue my request

        transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {
            @SneakyThrows
            @Override
            public void accept(TransactionStatus transactionStatus) {
                AbstractUser<Serializable> user = getUserService().findById(getSecondUser().getId()).get();
                user.setContactInformation(NEW_CONTACT_INFORMATION);
                getUserService().fullUpdate(user);
            }
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
