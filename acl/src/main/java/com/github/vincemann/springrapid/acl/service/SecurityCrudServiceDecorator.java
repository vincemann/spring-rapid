package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.CrudServiceDecorator;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SecurityCrudServiceDecorator<S extends CrudService<E, Id>, E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends CrudServiceDecorator<S, E, Id> {

    private AclTemplate aclTemplate;

    public SecurityCrudServiceDecorator(S decorated) {
        super(decorated);
    }

    @Override
    public Optional<E> findById(Id id) {
        Optional<E> entity = super.findById(id);
        entity.ifPresent(e -> getAclTemplate().checkPermission(e, BasePermission.READ));
        return entity;
    }

    @Override
    public E findPresentById(Id id) throws EntityNotFoundException {
        E entity = super.findPresentById(id);
        aclTemplate.checkPermission(entity, BasePermission.READ);
        return super.findPresentById(id);
    }

    @Override
    public Set<E> findSome(Set<Id> ids) {
        Set<E> entities = super.findSome(ids);
        entities.forEach(entity -> getAclTemplate().checkPermission(entity,BasePermission.READ));
        return entities;
    }

    @Override
    public Set<E> findAll() {
        Set<E> entities = super.findAll();
        entities.forEach(entity -> getAclTemplate().checkPermission(entity,BasePermission.READ));
        return entities;
    }

    @Override
    public Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> entityFilters, List<SortingExtension> sortingStrategies) {
        Set<E> entities = super.findAll(jpqlFilters,entityFilters,sortingStrategies);
        entities.forEach(entity -> getAclTemplate().checkPermission(entity,BasePermission.READ));
        return entities;
    }


    @Override
    public void deleteById(Id id) throws EntityNotFoundException {
        Optional<E> entity = super.findById(id);
        entity.ifPresent(e -> getAclTemplate().checkPermission(e,BasePermission.DELETE));
        super.deleteById(id);
    }

    protected AclTemplate getAclTemplate() {
        return aclTemplate;
    }

    @Autowired
    public void setAclTemplate(AclTemplate aclTemplate) {
        this.aclTemplate = aclTemplate;
    }
}
