package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.sec.bruteforce.LoginAttemptService;
import com.github.vincemann.springrapid.auth.sec.bruteforce.LoginBruteForceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@ConditionalOnProperty(name = "rapid-auth.bruteforce-protection", havingValue = "true")
public class BruteForceProtectionConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final LoginAttemptService loginAttemptService;
    private final AuthProperties properties;

    @Autowired
    public BruteForceProtectionConfigurer(LoginAttemptService loginAttemptService, AuthProperties properties) {
        this.loginAttemptService = loginAttemptService;
        this.properties = properties;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        LoginBruteForceFilter loginBruteForceFilter = new LoginBruteForceFilter(loginAttemptService, properties);
        http.addFilterBefore(loginBruteForceFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
