package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@LogInteraction
public interface HttpTokenService extends AopLoggable {
    public String extractToken(HttpServletRequest request);
    public void appendToken(String token ,HttpServletResponse response);
}
