package com.github.vincemann.springrapid.acl;

import com.github.vincemann.springrapid.core.security.AbstractRapidSecurityContext;
import com.github.vincemann.springrapid.core.security.AuthenticatedPrincipalImpl;
import lombok.Getter;
import lombok.Setter;

/**
 * Holds {@link AclEvaluationContext}, that will get set for each acl evaluation in {@link RapidAclSecurityChecker}.
 */
@Getter
@Setter
public class RapidAclSecurityContext<P extends AuthenticatedPrincipalImpl> extends AbstractRapidSecurityContext<P> {
    private AclEvaluationContext aclContext;

    public void clearAclContext(){
        this.aclContext = null;
    }
}
