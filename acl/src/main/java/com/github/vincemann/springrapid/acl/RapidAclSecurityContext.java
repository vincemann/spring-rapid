package com.github.vincemann.springrapid.acl;

import com.github.vincemann.springrapid.core.sec.RapidSecurityContextImpl;
import lombok.Getter;
import lombok.Setter;

/**
 * Holds {@link AclEvaluationContext}, that will get set for each acl evaluation in {@link AclSecurityCheckerImpl}.
 */
@Getter
@Setter
public class RapidAclSecurityContext extends RapidSecurityContextImpl {
    private AclEvaluationContext aclContext;

    public void clearAclContext(){
        this.aclContext = null;
    }
}
