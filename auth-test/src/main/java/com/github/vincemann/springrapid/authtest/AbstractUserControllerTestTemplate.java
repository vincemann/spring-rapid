package com.github.vincemann.springrapid.authtest;

import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.dto.*;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.util.AopProxyUtils;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.Serializable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Activate spring Security, so login endpoint and auth web config is enabled, when using this template.
 *
 * @Override
 * protected DefaultMockMvcBuilder createMvcBuilder() {
 *      DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
 *      mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
 *      return mvcBuilder;
 * }
 *
 * @param <C> UserController type
 */
public abstract class AbstractUserControllerTestTemplate<C extends AbstractUserController>
        extends CrudControllerTestTemplate<C> {
    /**
     * needs to be mocked and put into context
     * just use {@link org.springframework.boot.test.mock.mockito.MockBean}
     */
    private MessageSender messageSenderMock;

    @Autowired
    public void setMessageSenderMock(MessageSender messageSenderMock) {
        this.messageSenderMock = messageSenderMock;
    }

    @Override
    public void setMvc(MockMvc mvc) {
        super.setMvc(mvc);
    }

    public MockHttpServletRequestBuilder signup(SignupDto dto) throws Exception {
        return post(getController().getSignupUrl())
                .content(serialize(dto))
                .contentType(MediaType.APPLICATION_JSON);
    }

    public AuthMessage signup2xx(SignupDto dto) throws Exception {
        mvc.perform(signup(dto))
                .andExpect(status().is2xxSuccessful());
        return verifyMsgWasSent(dto.getContactInformation());
    }

    public MockHttpServletRequestBuilder resendVerificationMessage(Serializable id, String token) throws Exception {
        return post(getController().getResendVerificationMessageUrl())
                .param("id",id.toString())
                .header(HttpHeaders.AUTHORIZATION, token);
    }

    public String login(AbstractUser user) throws Exception {
        return mvc.perform(login(user.getContactInformation(),user.getPassword()))
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.AUTHORIZATION);
    }


    public MockHttpServletRequestBuilder changeContactInformation(String code, String token) {
        return post(getController().getChangeContactInformationUrl())
                .param("code", code)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public MockHttpServletRequestBuilder changeContactInformationWithLink(String link, String token) {
        return post(link)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }


    public MockHttpServletRequestBuilder requestContactInformationChange(String token, RequestContactInformationChangeDto dto) throws Exception {
        return post(getController().getRequestContactInformationChangeUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(serialize(dto));
    }

    public AuthMessage requestContactInformationChange2xx(String token, RequestContactInformationChangeDto dto) throws Exception {
        mvc.perform(post(getController().getRequestContactInformationChangeUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(serialize(dto)))
                .andExpect(status().is2xxSuccessful());
        return verifyMsgWasSent(dto.getOldContactInformation());
    }

    public MockHttpServletRequestBuilder changePassword(String token, ChangePasswordDto dto) throws Exception {
        return post(getController().getChangePasswordUrl())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(dto));
    }

    public MockHttpServletRequestBuilder forgotPassword(String contactInformation) throws Exception {
        return post(getController().getForgotPasswordUrl())
                .param("ci", contactInformation)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public AuthMessage forgotPassword2xx(String contactInformation) throws Exception {
        mvc.perform(post(getController().getForgotPasswordUrl())
                .param("ci", contactInformation)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is2xxSuccessful());
        return verifyMsgWasSent(contactInformation);
    }

    public MockHttpServletRequestBuilder resetPassword(ResetPasswordDto dto) throws Exception {
        return post(getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(dto));
    }

    public MockHttpServletRequestBuilder getResetPasswordView(String link) throws Exception {
        return get(link);
    }

    public MockHttpServletRequestBuilder resetPassword(String url, ResetPasswordDto dto) throws Exception {
        return post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(dto));
    }

    public MockHttpServletRequestBuilder fetchNewToken(String token) throws Exception {
        return post(getController().getFetchNewAuthTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public MockHttpServletRequestBuilder fetchNewToken(String token, String contactInformation) throws Exception {
        return post(getController().getFetchNewAuthTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("ci", contactInformation)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public String fetchNewToken2xx(String token, String contactInformation) throws Exception {
        return deserialize(mvc.perform(fetchNewToken(token, contactInformation))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), ResponseToken.class).getToken();
    }

    public String fetchNewToken2xx(String token) throws Exception {
        return deserialize(mvc.perform(fetchNewToken(token))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), ResponseToken.class).getToken();
    }


    public MockHttpServletRequestBuilder login(String contactInformation, String password) {
        return post(getController().getLoginUrl())
                .param("username", contactInformation)
                .param("password", password)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public MockHttpServletRequestBuilder findByContactInformation(String contactInformation) throws Exception {
        return get(getController().getFindByContactInformationUrl())
                .param("ci", contactInformation)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public String login2xx(String contactInformation, String password) throws Exception {
        return getMvc().perform(login(contactInformation, password))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

    public MockHttpServletRequestBuilder verifyContactInformation(String code) throws Exception {
        return get(getController().getVerifyUserUrl())
                .param("code", code)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public RequestBuilder verifyContactInformationWithLink(String link) throws Exception {
        return get(link)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public AuthMessage verifyMsgWasSent(String recipient) {
        ArgumentCaptor<AuthMessage> msgCaptor = ArgumentCaptor.forClass(AuthMessage.class);

        verify(AopProxyUtils.getUltimateTargetObject(messageSenderMock), atLeast(1))
                .send(msgCaptor.capture());
        AuthMessage sentData = msgCaptor.getValue();
        assertThat("latest msg must be sent to recipient: " +recipient + " but was sent to: " + sentData.getRecipient(),
                sentData.getRecipient(),equalTo(recipient));
        Mockito.reset(AopProxyUtils.getUltimateTargetObject(messageSenderMock));
        return sentData;
    }


    public MockHttpServletRequestBuilder resendVerificationContactInformation(String contactInformation, String token) throws Exception {
        return post(getController().getResendVerificationMessageUrl())
                .param("ci", contactInformation)
                .header(HttpHeaders.AUTHORIZATION, token);
    }

    public AuthMessage resendVerificationContactInformation2xx(String contactInformation, String token) throws Exception {
        mvc.perform(post(getController().getResendVerificationMessageUrl())
                .param("ci", contactInformation)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful());
        return verifyMsgWasSent(contactInformation);
    }

    public String login2xx(AbstractUser user) throws Exception {
        return login2xx(user.getContactInformation(), user.getPassword());
    }

    public MockHttpServletRequestBuilder testToken(String token) {
        return get(controller.getTestTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, token);
    }

    public MockHttpServletRequestBuilder blockUser(String ci) {
        return get(controller.getBlockUserUrl())
                .param("ci",ci);
    }


    public static class ResponseToken {

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}
