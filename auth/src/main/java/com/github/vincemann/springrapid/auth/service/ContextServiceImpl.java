package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.AuthPrincipal;
import com.github.vincemann.springrapid.auth.RapidSecurityContext;
import com.github.vincemann.springrapid.auth.Roles;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class ContextServiceImpl implements ContextService{

    private AuthProperties properties;
    @Override
    public Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("shared", properties.getShared());
        AuthPrincipal principal = RapidSecurityContext.currentPrincipal();
        if (principal != null) {
            if (!principal.getRoles().contains(Roles.ANON)) {
                AuthPrincipal withoutPw = new AuthPrincipal(principal);
                withoutPw.setPassword(null);
                context.put("user", withoutPw);
            }
        }

        return context;
    }

    @Autowired
    public void setProperties(AuthProperties properties) {
        this.properties = properties;
    }
}
