package com.github.vincemann.springlemon.auth.service.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AuthHeaderHttpTokenService implements HttpTokenService {

    @Override
    public String extractToken(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Override
    public void appendToken(String token, HttpServletResponse response) {
        response.addHeader(HttpHeaders.AUTHORIZATION, token);
    }
}
