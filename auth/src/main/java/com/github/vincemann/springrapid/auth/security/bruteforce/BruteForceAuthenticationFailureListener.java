package com.github.vincemann.springrapid.auth.security.bruteforce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class BruteForceAuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private LoginAttemptService loginAttemptService;

    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        WebAuthenticationDetails auth = (WebAuthenticationDetails)
                e.getAuthentication().getDetails();

        loginAttemptService.loginFailed(auth.getRemoteAddress());
    }

    @Autowired
    public void injectLoginAttemptService(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

}
