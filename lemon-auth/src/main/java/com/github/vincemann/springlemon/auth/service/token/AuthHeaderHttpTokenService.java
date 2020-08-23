package com.github.vincemann.springlemon.auth.service.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AuthHeaderHttpTokenService implements HttpTokenService {

//    public static final String TOKEN_PREFIX = "Bearer ";
//    public static final int TOKEN_PREFIX_LENGTH = 7;
//    public static final String TOKEN_RESPONSE_HEADER_NAME = "Lemon-Authorization";

    @Override
    public String extractToken(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);

//        if (header != null && header.startsWith(TOKEN_PREFIX)) {
//            // token present
//            return header.substring(TOKEN_PREFIX_LENGTH);
//        }
    }

    @Override
    public void appendToken(String token, HttpServletResponse response) {
        response.addHeader(HttpHeaders.AUTHORIZATION, token);
    }
}
