package com.github.vincemann.springlemon.auth.service.token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpTokenService {
    public String extractToken(HttpServletRequest request);
    public void appendToken(String token ,HttpServletResponse response);
}
