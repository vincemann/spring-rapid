package com.github.vincemann.springrapid.auth.sec;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import org.springframework.lang.Nullable;

public interface GlobalSecurityRule {

    /**
     * Is called whenever acl expression is evaluated by {@link GlobalRuleEnforcingAclPermissionEvaluator aclPermissionEvalutor}.
     * Throw {@link org.springframework.security.access.AccessDeniedException} or similar, when access is denied.
     * @param entity entity permission check is performed on
     * @param permission permission checked for
     * @return True if access allowed and further checks skipped. False if access denied and further checks skipped.
     *         Null if further checks should be performed and this rule effectively skips this case.
     * @see AdminGlobalSecurityRule
     */
    @Nullable
    Boolean checkAccess(IdAwareEntity<?> entity, Object permission);
}
