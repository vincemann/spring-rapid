package com.github.vincemann.springrapid.acl.plugin;

import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.SimpleCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract Acl Extension, granting {@link BasePermission#ADMINISTRATION} for creator and admin(role) of entity saved by {@link com.github.vincemann.springrapid.core.service.CrudService}.
 */
@Slf4j
@Getter
@Transactional
public class AdminFullAccessAclServiceExtension
        extends AbstractAclServiceExtension<CrudService> implements SimpleCrudServiceExtension<CrudService> {


    public AdminFullAccessAclServiceExtension(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService) {
        super(permissionService, mutableAclService, mockAuthService);
    }


    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().save(entity);
        saveFullPermissionForAdminOver(saved);
        return saved;
    }

}
