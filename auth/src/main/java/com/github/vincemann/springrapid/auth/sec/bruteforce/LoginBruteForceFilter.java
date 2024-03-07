package com.github.vincemann.springrapid.auth.sec.bruteforce;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.msg.mail.SmtpMailSender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginBruteForceFilter extends OncePerRequestFilter {


    private final Log log = LogFactory.getLog(LoginBruteForceFilter.class);
    private LoginAttemptService loginAttemptService;
    private AuthProperties properties;

    public LoginBruteForceFilter(LoginAttemptService loginAttemptService, AuthProperties properties) {
        this.loginAttemptService = loginAttemptService;
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // never check for X-Forwarded-For header
        String clientIP = request.getRemoteAddr();
        if (loginAttemptService.isBlocked(clientIP)){
            log.warn("User with ip: " +clientIP + " has too many unsuccessful login-tries -> is blocked now for one day");
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),"Client is blocked for one day");
            return;
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !path.equals(properties.getController().getLoginUrl());
    }

}
