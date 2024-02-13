package com.github.vincemann.springrapid.acl.service.ext.acl;

import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for acl info managing {@link ServiceExtension}s.
 *
 * @param <S>
 */
@Getter
public abstract class AclExtension<S>
        extends ServiceExtension<S> {

    protected RapidAclService rapidAclService;

    public AclExtension() {
    }

    @Autowired
    public void setAclPermissionService(RapidAclService rapidAclService) {
        this.rapidAclService = rapidAclService;
    }
}
