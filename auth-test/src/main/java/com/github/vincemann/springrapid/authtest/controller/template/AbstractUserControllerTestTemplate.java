package com.github.vincemann.springrapid.authtest.controller.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.RequestEmailChangeDto;
import com.github.vincemann.springrapid.auth.security.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.util.JsonUtils;
import com.github.vincemann.springrapid.coretest.controller.template.AbstractCrudControllerTestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Activate spring Security, so login endpoint and auth web config is enabled, when using this template.
 *
 *  @Override
 *     protected DefaultMockMvcBuilder createMvcBuilder() {
 *         DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
 *         mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
 *         return mvcBuilder;
 *     }
 * @param <C>
 */
public abstract class AbstractUserControllerTestTemplate<C extends AbstractUserController>
        extends AbstractCrudControllerTestTemplate<C> {


    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;
    private RapidSecurityContext<RapidAuthenticatedPrincipal> rapidSecurityContext;


    @Override
    public void setMvc(MockMvc mvc) {
        super.setMvc(mvc);
    }

    public MockHttpServletRequestBuilder signup(Object dto) throws Exception {
        return post(getController().getAuthProperties().getController().getSignupUrl())
                .content(serialize(dto))
                .contentType(getController().getCoreProperties().getController().getMediaType());
    }

//    public MockHttpServletRequestBuilder login(String email, String password) {
//        return login(new LoginDto(email,password));
//    }

    public ResultActions login(String email, String password) throws Exception {
        return getMvc().perform(login_raw(email,password));
    }

    public ResultActions requestEmailChange(Serializable targetId, String token, Object requestNewEmailDto) throws Exception {
        return getMvc().perform(post(getController().getAuthProperties().getController().getRequestEmailChangeUrl())
                .param("id",targetId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(serialize(requestNewEmailDto)));
//                .andExpect(status().is(204));
    }


    protected MockHttpServletRequestBuilder login_raw(String email, String password) {
        return post(getController().getAuthProperties().getController().getLoginUrl())
                .param("username", email)
                .param("password", password)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);

    }

    public String login2xx(String email, String password) throws Exception {
        return getMvc().perform(login_raw(email,password))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

    public String login2xx(AbstractUser user) throws Exception {
        return login2xx(user.getEmail(), user.getPassword());
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


    // todo add more methods there for each endpoint
}
