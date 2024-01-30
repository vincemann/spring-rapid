package com.github.vincemann.springrapid.acl.service.ext.acl;

import com.github.vincemann.springrapid.acl.service.AclPermissionService;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for acl info managing {@link BasicServiceExtension}s.
 *
 * @param <S>
 */
@Getter
public abstract class AclExtension<S>
        extends BasicServiceExtension<S> {

    protected AclPermissionService aclPermissionService;

    @Autowired
    public void injectAclPermissionService(AclPermissionService aclPermissionService) {
        this.aclPermissionService = aclPermissionService;
    }
}
