package com.github.vincemann.springrapid.acl.service.extensions.security;

import com.github.vincemann.springrapid.acl.AclSecurityChecker;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

/**
 *
 * Write extensions for secured Services
 * @see com.github.vincemann.springrapid.core.proxy.BasicServiceExtension
 */
@Getter
public abstract class AbstractSecurityExtension<T>
        extends BasicServiceExtension<T> {

    protected AclSecurityChecker securityChecker;

    @Autowired
    public void injectAclSecurityChecker(AclSecurityChecker securityChecker) {
        this.securityChecker = securityChecker;
    }
}
