package com.github.vincemann.springrapid.acl.service.ext.sec;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Does basic acl permission checks on crud Methods defined in {@link com.github.vincemann.springrapid.core.service.CrudService}.
 */
@Transactional
public class CrudAclChecksExtension
        extends SecurityExtension<CrudService>
                implements CrudServiceExtension<CrudService> {


    @Override
    public Optional findById(Serializable id) {
        getAclTemplate().checkPermission(id,getLast().getEntityClass(), BasePermission.READ);
        return getNext().findById(id);
    }


//    @Override
//    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
//        getSecurityChecker().checkPermission(entity, BasePermission.CREATE);
//        return getNext().save(entity);
//    }

    @Override
    public IdentifiableEntity partialUpdate(IdentifiableEntity entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(entity,BasePermission.WRITE);
        return getNext().partialUpdate(entity, fieldsToUpdate);
    }

    @Override
    public IdentifiableEntity fullUpdate(IdentifiableEntity entity) throws BadEntityException, EntityNotFoundException {
        getAclTemplate().checkPermission(entity,BasePermission.WRITE);
        return getNext().fullUpdate(entity);
    }

    @Override
    public Set<IdentifiableEntity> findSome(Set ids) {
        Set<IdentifiableEntity> entities = getNext().findSome(ids);
        entities.stream().forEach(entity -> getAclTemplate().checkPermission(entity,BasePermission.READ));
//        return getSecurityChecker().filter(entities,BasePermission.READ);
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
        getAclTemplate().checkPermission(id,getLast().getEntityClass(),BasePermission.DELETE);
        getNext().deleteById(id);
    }

}
