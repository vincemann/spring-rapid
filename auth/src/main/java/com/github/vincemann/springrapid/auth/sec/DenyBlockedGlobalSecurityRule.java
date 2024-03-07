package com.github.vincemann.springrapid.auth.sec;


import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.sec.AuthorizationTemplate;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import org.springframework.lang.Nullable;

public class DenyBlockedGlobalSecurityRule implements GlobalSecurityRule {

    @Nullable
    @Override
    public Boolean checkAccess(IdentifiableEntity<?> entity, Object permission, RapidSecurityContext securityContext) {
        AuthorizationTemplate.assertNotHasRoles(AuthRoles.BLOCKED);
        return null;
    }

}
