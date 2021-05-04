package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Does basic acl permission checks on crud Methods defined in {@link com.github.vincemann.springrapid.core.service.CrudService}.
 */
@Transactional
public class CrudAclChecksSecurityExtension
        extends SecurityServiceExtension<CrudService>
                implements CrudServiceExtension<CrudService> {


    @LogInteraction(Severity.DEBUG)
    @Override
    public Optional findById(Serializable id) throws BadEntityException {
        getSecurityChecker().checkPermission(id,getLast().getEntityClass(),getReadPermission());
        return getNext().findById(id);
    }

    @LogInteraction(Severity.DEBUG)
    @Override
    public IdentifiableEntity update(IdentifiableEntity entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(entity.getId(),getLast().getEntityClass(),getWritePermission());
        return getNext().update(entity,full);
    }

    @LogInteraction(Severity.DEBUG)
    @Override
    public Set findAll() {
        Set<IdentifiableEntity> entities = getNext().findAll();
        return getSecurityChecker().filter(entities,getReadPermission());
    }

    @LogInteraction(Severity.DEBUG)
    @Override
    public void deleteById(Serializable id) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(id,getLast().getEntityClass(),getDeletePermission());
        getNext().deleteById(id);
    }

}
