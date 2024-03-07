package com.github.vincemann.springrapid.auth.sec.bruteforce;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class BruteForceAuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final Log log = LogFactory.getLog(getClass());
    private LoginAttemptService loginAttemptService;

    public void onApplicationEvent(AuthenticationSuccessEvent e) {
        WebAuthenticationDetails auth = (WebAuthenticationDetails)
                e.getAuthentication().getDetails();
        if (auth == null){
            log.warn("no web authentication details available, skipping");
        }

        loginAttemptService.loginSucceeded(auth.getRemoteAddress());
    }

    @Autowired
    public void setLoginAttemptService(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }
}
