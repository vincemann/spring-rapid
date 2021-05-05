package com.github.vincemann.springrapid.acl.service.extensions;

import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.acl.service.extensions.AbstractAclExtension;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Getter
@Transactional
@ServiceComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AdminFullAccessAboutSavedAclExtension
        extends RoleHasPermissionAboutSavedAclExtension{

    public AdminFullAccessAboutSavedAclExtension() {
        super(Roles.ADMIN, BasePermission.ADMINISTRATION);
    }

    @LogInteraction
    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        return getNext().save(entity);
    }

}
