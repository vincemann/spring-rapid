package com.github.vincemann.springrapid.acl.service.extensions.security;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.access.AccessDeniedException;


public class OnlyRoleCanSaveSecurityExtension
        extends SecurityServiceExtension<CrudService>
        implements CrudServiceExtension<CrudService> {

    private String role;

    public OnlyRoleCanSaveSecurityExtension(String role) {
        this.role = role;
    }

    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        if (!RapidSecurityContext.getRoles().contains(role)){
            throw new AccessDeniedException("Need role: " + role + " to create entity of this type");
        }
        return getNext().save(entity);
    }
}
