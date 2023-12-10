package com.github.vincemann.springrapid.acl.service.extensions.security;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Does basic acl permission checks on crud Methods defined in {@link com.github.vincemann.springrapid.core.service.CrudService}.
 */
@Transactional
public class CrudAclChecksSecurityExtension
        extends AbstractSecurityExtension<CrudService>
                implements CrudServiceExtension<CrudService> {


    @Override
    public Optional findById(Serializable id) throws BadEntityException {
        getSecurityChecker().checkPermission(id,getLast().getEntityClass(), BasePermission.READ);
        return getNext().findById(id);
    }


//    @Override
//    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
//        getSecurityChecker().checkPermission(entity, BasePermission.CREATE);
//        return getNext().save(entity);
//    }

    @Override
    public IdentifiableEntity partialUpdate(IdentifiableEntity entity, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(entity,BasePermission.WRITE);
        return getNext().partialUpdate(entity,fieldsToRemove);
    }

    @Override
    public IdentifiableEntity fullUpdate(IdentifiableEntity entity) throws BadEntityException, EntityNotFoundException {
        getSecurityChecker().checkPermission(entity,BasePermission.WRITE);
        return getNext().fullUpdate(entity);
    }

    @Override
    public Set findAll() {
        Set<IdentifiableEntity> entities = getNext().findAll();
        return getSecurityChecker().filter(entities,BasePermission.READ);
    }

    @Override
    public void deleteById(Serializable id) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(id,getLast().getEntityClass(),BasePermission.DELETE);
        getNext().deleteById(id);
    }

}
