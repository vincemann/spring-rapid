package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static com.github.vincemann.springrapid.auth.util.PrincipalUtils.isAnon;

public class ContextServiceImpl implements ContextService{

    private AuthProperties properties;
    @Override
    public Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("shared", properties.getShared());
        RapidPrincipal principal = RapidSecurityContext.currentPrincipal();
        if (principal != null) {
            if (!isAnon(principal)) {
                RapidPrincipal withoutPw = new RapidPrincipal(principal);
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
