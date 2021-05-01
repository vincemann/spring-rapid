package com.github.vincemann.springrapid.authtest.controller.template;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.security.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.coretest.controller.template.AbstractCrudControllerTestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public abstract class AbstractUserControllerTestTemplate<C extends AbstractUserController>
        extends AbstractCrudControllerTestTemplate<C> {



    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;
    private RapidSecurityContext<RapidAuthenticatedPrincipal> rapidSecurityContext;


    public MockHttpServletRequestBuilder signup(Object dto) throws Exception {
        return post(getController().getAuthProperties().getController().getSignupUrl())
                .content(serialize(dto))
                .contentType(getController().getCoreProperties().getController().getMediaType());
    }

    public MockHttpServletRequestBuilder login(LoginDto loginDto) {
        return post(getController().getAuthProperties().getController().getLoginUrl())
                .param("username", loginDto.getEmail())
                .param("password", loginDto.getPassword())
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);

    }

    public MockHttpServletRequestBuilder login(AbstractUser user) {
        return post(getController().getAuthProperties().getController().getLoginUrl())
                .param("username", user.getEmail())
                .param("password", user.getPassword())
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);

    }

    public void mockLogin(AbstractUser user){
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
