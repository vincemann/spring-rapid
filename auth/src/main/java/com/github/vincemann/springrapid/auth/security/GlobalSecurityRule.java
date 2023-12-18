package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.acl.AclEvaluationContext;

public interface GlobalSecurityRule {

    /**
     * Is called whenever acl is checked -> i.E. check if user a has read permission over target entity x
     * get user a and more from {@link com.github.vincemann.springrapid.core.security.RapidSecurityContext}.
     *
     * Check what you need to check and throw runtime exception like {@link org.springframework.security.access.AccessDeniedException} if needed
     * you can also return true,false meaning access allowed, denied respectively. Otherwise return null
     */
    Boolean checkAccess(AclEvaluationContext aclContext);

}
