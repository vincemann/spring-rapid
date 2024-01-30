package com.github.vincemann.springrapid.acl.service.ext.sec;

import com.github.vincemann.springrapid.acl.AclSecurityChecker;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * Write extensions for secured Services
 * @see com.github.vincemann.springrapid.core.proxy.BasicServiceExtension
 */
@Getter
public abstract class SecurityExtension<T>
        extends BasicServiceExtension<T> {

    protected AclSecurityChecker securityChecker;

    @Autowired
    public void injectAclSecurityChecker(AclSecurityChecker securityChecker) {
        this.securityChecker = securityChecker;
    }
}
