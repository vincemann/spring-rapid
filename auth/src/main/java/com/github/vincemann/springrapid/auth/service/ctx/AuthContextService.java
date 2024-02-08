package com.github.vincemann.springrapid.auth.service.ctx;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.ctx.CoreContextService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.github.vincemann.springrapid.auth.util.PrincipalUtils.isAnon;

public class AuthContextService extends CoreContextService {

    private RapidSecurityContext securityContext;

    @Override
    public Map<String, Object> getContext() {
        Map<String, Object> context = super.getContext();
        RapidPrincipal principal = securityContext.currentPrincipal();
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
    public void setSecurityContext(RapidSecurityContext securityContext) {
        this.securityContext = securityContext;
    }
}
