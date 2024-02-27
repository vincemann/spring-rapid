package com.github.vincemann.springrapid.acl.service.ext.sec;

import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.DefaultExtension;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Does basic acl permission checks on crud Methods defined in {@link com.github.vincemann.springrapid.core.service.CrudService}
 * create is allowed by default (you can combine with {@link NeedCreatePermissionOnParentForCreateExtension}.
 * update operations require write permission
 * read operations required read permission
 * delete operations required delete permission
 */
@DefaultExtension(qualifier = Secured.class, service = CrudService.class)
public class CrudAclChecksExtension
        extends SecurityExtension<CrudService>
                implements CrudServiceExtension<CrudService> {


    @Override
    public Optional findById(Serializable id) {
        Optional byId = getLast().findById(id);
        if (byId.isEmpty())
            return getNext().findById(id);
        getAclTemplate().checkPermission(id,getLast().getEntityClass(), BasePermission.READ);
        return getNext().findById(id);
    }

    @Override
    public IdentifiableEntity findPresentById(Serializable id) throws EntityNotFoundException {
        Optional byId = getLast().findById(id);
        if (byId.isEmpty())
            throw new EntityNotFoundException(id,getLast().getEntityClass());
        getAclTemplate().checkPermission(id,getLast().getEntityClass(), BasePermission.READ);
        return getNext().findPresentById(id);
    }

    @Override
    public IdentifiableEntity partialUpdate(IdentifiableEntity update, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException {
        Optional byId = getLast().findById(update.getId());
        if (byId.isEmpty())
            throw new EntityNotFoundException(update.getId(),getLast().getEntityClass());
        getAclTemplate().checkPermission(update,BasePermission.WRITE);
        return getNext().partialUpdate(update, fieldsToUpdate);
    }

    @Override
    public IdentifiableEntity fullUpdate(IdentifiableEntity update) throws BadEntityException, EntityNotFoundException {
        Optional byId = getLast().findById(update.getId());
        if (byId.isEmpty())
            throw new EntityNotFoundException(update.getId(),getLast().getEntityClass());
        getAclTemplate().checkPermission(update,BasePermission.WRITE);
        return getNext().fullUpdate(update);
    }

    @Override
    public Set<IdentifiableEntity> findSome(Set ids) {
        Set<IdentifiableEntity> entities = getNext().findSome(ids);
        entities.stream().forEach(entity -> getAclTemplate().checkPermission(entity,BasePermission.READ));
        return entities;
    }

    @Override
    public Set findAll(List jpqlFilters, List entityFilters, List sortingStrategies) {
        Set<IdentifiableEntity> entities = getNext().findAll(jpqlFilters,entityFilters,sortingStrategies);
        entities.stream().forEach(entity -> getAclTemplate().checkPermission(entity,BasePermission.READ));
        return entities;
    }

    @Override
    public Set findAll() {
        Set<IdentifiableEntity> entities = getNext().findAll();
        entities.stream().forEach(entity -> getAclTemplate().checkPermission(entity,BasePermission.READ));
        return entities;
    }

    @Override
    public void deleteById(Serializable id) throws EntityNotFoundException {
        Optional byId = getLast().findById(id);
        if (byId.isEmpty())
            throw new EntityNotFoundException(id,getLast().getEntityClass());
        getAclTemplate().checkPermission(id,getLast().getEntityClass(),BasePermission.DELETE);
        getNext().deleteById(id);
    }

}
