package com.github.vincemann.springrapid.auth.security.bruteforce;

import com.github.vincemann.springrapid.auth.AuthProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginBruteForceFilter extends OncePerRequestFilter {

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
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),"Client is blocked for one day");
            return;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return !path.equals(properties.getController().getLoginUrl());
    }

}
