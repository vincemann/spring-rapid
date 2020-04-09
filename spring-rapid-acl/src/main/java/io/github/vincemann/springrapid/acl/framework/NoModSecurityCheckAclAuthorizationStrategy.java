package io.github.vincemann.springrapid.acl.framework;

import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.core.GrantedAuthority;

/**
 * Anyone is able to change Acls -> make sure modifying calls are only made by trusted code
 */
public class NoModSecurityCheckAclAuthorizationStrategy extends AclAuthorizationStrategyImpl {

    @Override
    public void securityCheck(Acl acl, int changeType) {
        //noop
    }

    public NoModSecurityCheckAclAuthorizationStrategy(GrantedAuthority... auths) {
        super(auths);
    }
}
