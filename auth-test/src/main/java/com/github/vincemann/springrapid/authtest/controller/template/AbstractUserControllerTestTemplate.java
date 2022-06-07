package com.github.vincemann.springrapid.authtest.controller.template;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordView;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.mail.MailSender;
import com.github.vincemann.springrapid.auth.security.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;

import com.github.vincemann.springrapid.coretest.controller.template.AbstractCrudControllerTestTemplate;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.Serializable;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Activate spring Security, so login endpoint and auth web config is enabled, when using this template.
 *
 * @param <C>
 * @Override protected DefaultMockMvcBuilder createMvcBuilder() {
 * DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
 * mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
 * return mvcBuilder;
 * }
 */
public abstract class AbstractUserControllerTestTemplate<C extends AbstractUserController>
        extends AbstractCrudControllerTestTemplate<C> {


    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;
    private RapidSecurityContext<RapidAuthenticatedPrincipal> rapidSecurityContext;
    private MailSender<MailData> mailSenderMock;




    @Autowired
    public void setMailSenderMock(MailSender<MailData> mailSenderMock) {
        this.mailSenderMock = mailSenderMock;
    }

    @Override
    public void setMvc(MockMvc mvc) {
        super.setMvc(mvc);
    }

    public RequestBuilder signup(Object dto) throws Exception {
        return post(getController().getAuthProperties().getController().getSignupUrl())
                .content(serialize(dto))
                .contentType(getController().getCoreProperties().getController().getMediaType());
    }

    public MailData signup2xx(Object dto) throws Exception {
        mvc.perform(signup(dto))
                .andExpect(status().is2xxSuccessful());
        return verifyMailWasSend();
    }

    public RequestBuilder resendVerificationMail(Serializable id, String token) throws Exception {
        return post(getController().getAuthProperties().getController().getResendVerificationContactInformationUrl())
                .param("id",id.toString())
                .header(HttpHeaders.AUTHORIZATION, token);
    }

//    public MockHttpServletRequestBuilder login(String contactInformation, String password) {
//        return login(new LoginDto(contactInformation,password));
//    }

    public String login(AbstractUser user) throws Exception {
        return mvc.perform(login_builder(user.getContactInformation(),user.getPassword()))
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.AUTHORIZATION);
    }

    public RequestBuilder login_builder(String contactInformation, String password) throws Exception {
        return login_raw(contactInformation, password);
    }


    public RequestBuilder changeContactInformation(String code, String token) throws Exception {
        return post(getController().getAuthProperties().getController().getChangeContactInformationUrl())
                .param("code", code)
//                .param("id", targetId.toString())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public RequestBuilder changeContactInformationWithLink(String link, String token) throws Exception {
        return post(link)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }


    public RequestBuilder requestContactInformationChange(Serializable targetId, String token, Object requestNewContactInformationDto) throws Exception {
        return post(getController().getAuthProperties().getController().getRequestContactInformationChangeUrl())
                .param("id", targetId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(serialize(requestNewContactInformationDto));
    }

    public MailData requestContactInformationChange2xx(Serializable targetId, String token, Object requestNewContactInformationDto) throws Exception {
        mvc.perform(post(getController().getAuthProperties().getController().getRequestContactInformationChangeUrl())
                .param("id", targetId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(serialize(requestNewContactInformationDto)))
                .andExpect(status().is2xxSuccessful());
        return verifyMailWasSend();
    }

    public RequestBuilder changePassword(Serializable id, String token, Object changePasswordDto) throws Exception {
        return post(getController().getAuthProperties().getController().getChangePasswordUrl())
                .param("id", id.toString())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(changePasswordDto));
    }

    public RequestBuilder forgotPassword(String contactInformation) throws Exception {
        return post(getController().getAuthProperties().getController().getForgotPasswordUrl())
                .param("contactInformation", contactInformation)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public MailData forgotPassword2xx(String contactInformation) throws Exception {
        mvc.perform(post(getController().getAuthProperties().getController().getForgotPasswordUrl())
                .param("contactInformation", contactInformation)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is2xxSuccessful());
        return verifyMailWasSend();
    }

    public RequestBuilder resetPassword(ResetPasswordView resetPasswordView, String code) throws Exception {
        return post(getController().getAuthProperties().getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("code", code)
                .content("password="+resetPasswordView.getPassword()+"&matchPassword="+resetPasswordView.getMatchPassword());
    }

    public RequestBuilder getResetPasswordView(String link) throws Exception {
        return get(link);
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(serialize(resetPasswordDto)));
    }

    public RequestBuilder resetPassword(String url, Object resetPasswordDto, String code) throws Exception {
        return post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("code", code)
                .content(serialize(resetPasswordDto));
    }

    public RequestBuilder fetchNewToken(String token) throws Exception {
        return post(getController().getAuthProperties().getController().getFetchNewAuthTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public RequestBuilder fetchNewToken(String token, String contactInformation) throws Exception {
        return post(getController().getAuthProperties().getController().getFetchNewAuthTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("contactInformation", contactInformation)
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

    protected MockHttpServletRequestBuilder login_raw(String contactInformation, String password) {
        return post(getController().getAuthProperties().getController().getLoginUrl())
                .param("username", contactInformation)
                .param("password", password)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public RequestBuilder fetchByContactInformation(String contactInformation) throws Exception {
        return post(getController().getAuthProperties().getController().getFetchByContactInformationUrl())
                .param("contactInformation", contactInformation)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public String login2xx(String contactInformation, String password) throws Exception {
        return getMvc().perform(login_raw(contactInformation, password))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

    public RequestBuilder verifyContactInformation(String code) throws Exception {
        return get(getController().getAuthProperties().getController().getVerifyUserUrl())
//                .param("id", id.toString())
                .param("code", code)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public RequestBuilder verifyContactInformationWithLink(String link) throws Exception {
        return get(link)
//                .param("id", id.toString())
//                .param("code", code)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public MailData verifyMailWasSend() {
        ArgumentCaptor<MailData> captor = ArgumentCaptor.forClass(MailData.class);
        verify(mailSenderMock, times(1)).send(captor.capture());
        MailData sentData = captor.getValue();
        Mockito.reset(mailSenderMock);
        return sentData;
    }


    public RequestBuilder resendVerificationContactInformation(String contactInformation, String token) throws Exception {
        return post(getController().getAuthProperties().getController().getResendVerificationContactInformationUrl())
                .param("contactInformation", contactInformation)
                .header(HttpHeaders.AUTHORIZATION, token);
    }

    public MailData resendVerificationContactInformation2xx(String contactInformation, String token) throws Exception {
        mvc.perform(post(getController().getAuthProperties().getController().getResendVerificationContactInformationUrl())
                .param("contactInformation", contactInformation)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful());
        return verifyMailWasSend();
    }

    public String login2xx(AbstractUser user) throws Exception {
        return login2xx(user.getContactInformation(), user.getPassword());
    }

    public void mockLogin(AbstractUser user) {
        rapidSecurityContext.login(authenticatedPrincipalFactory.create(user));
    }

    @Autowired
    public void setAuthenticatedPrincipalFactory(AuthenticatedPrincipalFactory authenticatedPrincipalFactory) {
        this.authenticatedPrincipalFactory = authenticatedPrincipalFactory;
    }

    @Autowired
    public void setRapidSecurityContext(RapidSecurityContext<RapidAuthenticatedPrincipal> rapidSecurityContext) {
        this.rapidSecurityContext = rapidSecurityContext;
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


    // todo add more methods there for each endpoint
}
