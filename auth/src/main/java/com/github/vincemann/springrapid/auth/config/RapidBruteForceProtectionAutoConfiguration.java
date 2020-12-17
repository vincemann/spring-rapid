package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.security.bruteforce.LoginAttemptService;
import com.github.vincemann.springrapid.auth.security.bruteforce.LoginBruteForceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

public class RapidBruteForceProtectionAutoConfiguration {

//    private LoginAttemptService loginAttemptService;
//    private AuthProperties authProperties;


//    @Bean
//    public FilterRegistrationBean someFilterRegistration() {
//
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new LoginBruteForceFilter(loginAttemptService));
//        registration.addUrlPatterns(authProperties.getController().getLoginUrl());
////        registration.addInitParameter("paramName", "paramValue");
//        registration.setName("bruteforce-login-filter");
//        registration.setOrder(1);
//        return registration;
//    }
//
//    @Bean(name = "someFilter")
//    public Filter someFilter() {
//        return new SomeFilter();
//    }

//    @Autowired
//    public void injectAuthProperties(AuthProperties authProperties) {
//        this.authProperties = authProperties;
//    }
//
//    @Autowired
//    public void injectLoginAttemptService(LoginAttemptService loginAttemptService) {
//        this.loginAttemptService = loginAttemptService;
//    }

}
