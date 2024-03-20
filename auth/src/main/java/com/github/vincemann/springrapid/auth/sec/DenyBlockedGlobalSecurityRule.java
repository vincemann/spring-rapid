package com.github.vincemann.springrapid.auth.sec;


import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.sec.AuthorizationUtils;
import org.springframework.lang.Nullable;

public class DenyBlockedGlobalSecurityRule implements GlobalSecurityRule {

    @Nullable
    @Override
    public Boolean checkAccess(IdAwareEntity<?> entity, Object permission) {
        AuthorizationUtils.assertNotHasRoles(AuthRoles.BLOCKED);
        return null;
    }

}
