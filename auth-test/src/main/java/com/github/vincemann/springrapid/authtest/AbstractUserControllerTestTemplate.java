package com.github.vincemann.springrapid.authtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.dto.*;
import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.RapidPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.Assert;

import static org.hamcrest.MatcherAssert.assertThat;
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
        extends MvcControllerTestTemplate<C> {

    @Autowired
    private RapidPasswordEncoder passwordEncoder;

    @Autowired
    private AuthProperties properties;

    @Override
    public void setMvc(MockMvc mvc) {
        super.setMvc(mvc);
    }

    public String login(AbstractUser<?> user) throws Exception {
        Assert.isTrue(!passwordEncoder.isEncoded(user.getPassword()),"password must not be encoded - need clear text password." +
                " Dont use saved user instance");
        return mvc.perform(login(user.getContactInformation(),user.getPassword()))
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.AUTHORIZATION);
    }


    public MockHttpServletRequestBuilder changeContactInformation(String code, String token) {
        return post(getController().getChangeContactInformationUrl())
                .param("code", code)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    public MockHttpServletRequestBuilder changeContactInformationWithLink(String link, String token) {
        return post(link)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }


    public MockHttpServletRequestBuilder requestContactInformationChange(RequestContactInformationChangeDto dto, String token) throws Exception {
        return post(getController().getRequestContactInformationChangeUrl())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(serialize(dto));
    }

    public void requestContactInformationChange2xx(RequestContactInformationChangeDto dto, String token) throws Exception {
        mvc.perform(post(getController().getRequestContactInformationChangeUrl())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(serialize(dto)))
                .andExpect(status().is2xxSuccessful());
    }

    public MockHttpServletRequestBuilder changePassword(ChangePasswordDto dto,String token) throws Exception {
        return post(getController().getChangePasswordUrl())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(serialize(dto));
    }

    public MockHttpServletRequestBuilder forgotPassword(String contactInformation) throws Exception {
        return post(getController().getForgotPasswordUrl())
                .param("ci", contactInformation)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    public void forgotPassword2xx(String contactInformation) throws Exception {
        mvc.perform(post(getController().getForgotPasswordUrl())
                .param("ci", contactInformation)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is2xxSuccessful());
    }

    public MockHttpServletRequestBuilder resetPassword(ResetPasswordDto dto) throws Exception {
        return post(getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(serialize(dto));
    }

    public MockHttpServletRequestBuilder getResetPasswordView(String link) throws Exception {
        return get(link);
    }

    public MockHttpServletRequestBuilder resetPassword(String url, ResetPasswordDto dto) throws Exception {
        return post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(serialize(dto));
    }

    public MockHttpServletRequestBuilder fetchNewToken(String token) throws Exception {
        return post(getController().getFetchNewAuthTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    public MockHttpServletRequestBuilder fetchNewToken(String token, String contactInformation) throws Exception {
        return post(getController().getFetchNewAuthTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("ci", contactInformation)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
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


    public MockHttpServletRequestBuilder login(String contactInformation, String password) throws JsonProcessingException {
        return post(properties.getLoginUrl())
                .content(serialize(new LoginDto(contactInformation,password)))
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }


    public String login2xx(String contactInformation, String password) throws Exception {
        return getMvc().perform(login(contactInformation, password))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

    public MockHttpServletRequestBuilder verifyUser(String code) throws Exception {
        return get(getController().getVerifyUserUrl())
                .param("code", code)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    public RequestBuilder verifyUserWithLink(String link) throws Exception {
        return get(link)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }


    public MockHttpServletRequestBuilder resendVerificationMsg(String contactInformation, String token) throws Exception {
        return post(getController().getResendVerificationMessageUrl())
                .param("ci", contactInformation)
                .header(HttpHeaders.AUTHORIZATION, token);
    }

    public void resendVerificationMsg2xx(String contactInformation, String token) throws Exception {
        mvc.perform(resendVerificationMsg(contactInformation,token))
                .andExpect(status().is2xxSuccessful());
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
