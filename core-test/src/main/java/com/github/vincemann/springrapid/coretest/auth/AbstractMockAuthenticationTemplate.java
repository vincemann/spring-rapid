package com.github.vincemann.springrapid.coretest.auth;

import com.github.vincemann.springrapid.core.controller.rapid.CurrentUserIdProvider;
import com.github.vincemann.springrapid.coretest.BeforeEachMethodInitializable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @see MockAuthenticationTemplate
 */
public abstract class AbstractMockAuthenticationTemplate implements MockAuthenticationTemplate, BeforeEachMethodInitializable {

    private Authentication authenticationMock;
    private SecurityContext securityContextMock;
    private CurrentUserIdProvider currentUserIdProviderMock;

    private boolean mocked;
    private SecurityContext realSecurityContext;
    private Authentication realAuthentication;
    private CurrentUserIdProvider realCurrentUserIdProvider;


    @Override
    public boolean isMocked() {
        return mocked;
    }

    @Override
    //before each
    public void init() {
        setUpAuthMocks();
    }

    @Autowired
    public void injectCurrentUserIdProviderMock(CurrentUserIdProvider currentUserIdProviderMock) {
        this.realCurrentUserIdProvider = currentUserIdProviderMock;
    }

    public void setUpAuthMocks() {
        realSecurityContext = SecurityContextHolder.getContext()==null
                ? SecurityContextHolder.createEmptyContext()
                : SecurityContextHolder.getContext();
        realAuthentication = realSecurityContext.getAuthentication();
        enableMocking();
    }

    @Override
    public void enableMocking(){
        authenticationMock = Mockito.spy(Authentication.class);
        // Mockito.whens() for your authorization object
        securityContextMock = Mockito.spy(SecurityContext.class);
        currentUserIdProviderMock=Mockito.spy(CurrentUserIdProvider.class);
        mocked =true;
    }

    @Override
    public void disableMocking(){
//        Mockito.reset(authenticationMock);
//        Mockito.reset(securityContextMock);
        SecurityContextHolder.setContext(realSecurityContext);
        realSecurityContext.setAuthentication(realAuthentication);
        mocked =false;
    }

    protected void mockAs(Authentication authentication){
        if(!mocked){
            enableMocking();
        }
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContextMock);
    }

    protected Authentication getAuthenticationMock() {
        return authenticationMock;
    }

    protected SecurityContext getSecurityContextMock() {
        return securityContextMock;
    }

    protected SecurityContext getRealSecurityContext() {
        return realSecurityContext;
    }

    protected Authentication getRealAuthentication() {
        return realAuthentication;
    }
}
