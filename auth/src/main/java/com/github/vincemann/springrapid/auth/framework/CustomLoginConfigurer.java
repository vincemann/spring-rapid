package com.github.vincemann.springrapid.auth.framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class CustomLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, CustomLoginConfigurer<H>, UsernamePasswordAuthenticationFilter>
{


    public CustomLoginConfigurer(ObjectMapper mapper) {
        super(new DtoUsernamePasswordAuthenticationFilter(mapper), null);
    }

    @Override
    public CustomLoginConfigurer<H> loginPage(String loginPage) {
        return super.loginPage(loginPage);
    }

    /**
     * Forward Authentication Failure Handler
     *
     * @param forwardUrl the target URL in case of failure
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public CustomLoginConfigurer<H> failureForwardUrl(String forwardUrl) {
        failureHandler(new ForwardAuthenticationFailureHandler(forwardUrl));
        return this;
    }

    /**
     * Forward Authentication Success Handler
     *
     * @param forwardUrl the target URL in case of success
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public CustomLoginConfigurer<H> successForwardUrl(String forwardUrl) {
        successHandler(new ForwardAuthenticationSuccessHandler(forwardUrl));
        return this;
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
        initDefaultLoginFilter(http);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.config.annotation.web.configurers.
     * AbstractAuthenticationFilterConfigurer
     * #createLoginProcessingUrlMatcher(java.lang.String)
     */
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    /**
     * Gets the HTTP parameter that is used to submit the username.
     *
     * @return the HTTP parameter that is used to submit the username
     */
    private String getUsernameParameter() {
        return getAuthenticationFilter().getUsernameParameter();
    }

    /**
     * Gets the HTTP parameter that is used to submit the password.
     *
     * @return the HTTP parameter that is used to submit the password
     */
    private String getPasswordParameter() {
        return getAuthenticationFilter().getPasswordParameter();
    }

    /**
     * If available, initializes the {@link DefaultLoginPageGeneratingFilter} shared
     * object.
     *
     * @param http the {@link HttpSecurityBuilder} to use
     */
    private void initDefaultLoginFilter(H http) {
        DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http
                .getSharedObject(DefaultLoginPageGeneratingFilter.class);
        if (loginPageGeneratingFilter != null && !isCustomLoginPage()) {
            loginPageGeneratingFilter.setFormLoginEnabled(true);
            loginPageGeneratingFilter.setUsernameParameter(getUsernameParameter());
            loginPageGeneratingFilter.setPasswordParameter(getPasswordParameter());
            loginPageGeneratingFilter.setLoginPageUrl(getLoginPage());
            loginPageGeneratingFilter.setFailureUrl(getFailureUrl());
            loginPageGeneratingFilter.setAuthenticationUrl(getLoginProcessingUrl());
        }
    }
}
