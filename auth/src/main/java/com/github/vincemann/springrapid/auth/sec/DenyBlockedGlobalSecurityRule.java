package com.github.vincemann.springrapid.auth.sec;


import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.sec.AuthorizationTemplate;
import org.springframework.lang.Nullable;

public class DenyBlockedGlobalSecurityRule implements GlobalSecurityRule {

    @Nullable
    @Override
    public Boolean checkAccess(IdAwareEntity<?> entity, Object permission) {
        AuthorizationTemplate.assertNotHasRoles(AuthRoles.BLOCKED);
        return null;
    }

}
