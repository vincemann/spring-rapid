package com.github.vincemann.springrapid.acldemo.service.ext.sec;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.acl.service.ext.sec.SecurityExtension;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.sec.Roles;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@LogInteraction(Severity.DEBUG)
public class NeedRoleForCreateExtension
        extends SecurityExtension<CrudService>
        implements CrudServiceExtension<CrudService> {

    private String role;

    public NeedRoleForCreateExtension(String role) {
        this.role = role;
    }

    @Override
    public IdentifiableEntity create(IdentifiableEntity entity) throws BadEntityException {
        if (!RapidSecurityContext.getRoles().contains(role) &&
                !RapidSecurityContext.getRoles().contains(Roles.ADMIN)){
            throw new AccessDeniedException("Need role: " + role + " to create entity of this type");
        }
        return getNext().create(entity);
    }
}
