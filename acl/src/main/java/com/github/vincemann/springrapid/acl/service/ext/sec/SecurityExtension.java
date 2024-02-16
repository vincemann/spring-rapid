package com.github.vincemann.springrapid.acl.service.ext.sec;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * indicates this extension is for {@link com.github.vincemann.springrapid.acl.proxy.Secured} - service
 */
@Getter
public abstract class SecurityExtension<T>
        extends ServiceExtension<T> {

    protected AclTemplate aclTemplate;

    @Autowired
    public void setAclSecurityChecker(AclTemplate securityChecker) {
        this.aclTemplate = securityChecker;
    }
}
