package com.github.vincemann.springrapid.acl.service.extensions;

import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Getter
@Transactional
@ServiceComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OwnerFullAccessAboutSavedAclExtension
        extends AbstractAclExtension<CrudService>
        implements CrudServiceExtension<CrudService>
{
    private DelegatingOwnerLocator delegatingOwnerLocator;

    @Autowired
    public OwnerFullAccessAboutSavedAclExtension(DelegatingOwnerLocator delegatingOwnerLocator) {
        this.delegatingOwnerLocator = delegatingOwnerLocator;
    }

    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().save(entity);
        Optional<String> optionalOwner = delegatingOwnerLocator.find(saved);
        if (optionalOwner.isEmpty()){
            throw new BadEntityException("Owner not found for entity: " + saved + " which is needed to give acl permission for");
        }
        savePermissionForUserOverEntity(optionalOwner.get(),saved, BasePermission.ADMINISTRATION);
        return saved;
    }
}
