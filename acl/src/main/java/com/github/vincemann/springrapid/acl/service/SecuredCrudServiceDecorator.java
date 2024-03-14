package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.core.controller.WebExtensionType;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.CrudServiceDecorator;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * adds basic acl permission checks to crud operations
 *
 * @param <S>  decorated crud service
 * @param <E>  entity type of crud service
 * @param <Id> id type of entity
 */
public class SecuredCrudServiceDecorator
        <
                S extends CrudService<E, Id>,
                E extends IdentifiableEntity<Id>,
                Id extends Serializable
                >
        extends CrudServiceDecorator<S, E, Id> {

    private AclTemplate aclTemplate;

    private List<FilterRule> filterRules = new ArrayList<>();

    public SecuredCrudServiceDecorator(S decorated) {
        super(decorated);
        configureFilterRules();
    }

    protected void configureFilterRules(){}

    protected void registerFilterRule(FilterRule rule){
        this.filterRules.add(rule);
    }

    @Override
    public E softUpdate(E entity) throws EntityNotFoundException {
        aclTemplate.checkPermission(entity, BasePermission.WRITE);
        return super.softUpdate(entity);
    }

    @Override
    public E partialUpdate(E update, String... fieldsToUpdate) throws EntityNotFoundException {
        aclTemplate.checkPermission(update, BasePermission.WRITE);
        return super.partialUpdate(update, fieldsToUpdate);
    }

    @Override
    public E fullUpdate(E update) throws EntityNotFoundException {
        aclTemplate.checkPermission(update, BasePermission.WRITE);
        return super.fullUpdate(update);
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
        entities.forEach(entity -> getAclTemplate().checkPermission(entity, BasePermission.READ));
        return entities;
    }

    @Override
    public Set<E> findAll() {
        Set<E> entities = super.findAll();
        entities.forEach(entity -> getAclTemplate().checkPermission(entity, BasePermission.READ));
        return entities;
    }

    @Override
    public Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> entityFilters, List<SortingExtension> sortingStrategies) {
        Set<E> entities = super.findAll(jpqlFilters, entityFilters, sortingStrategies);
        applyFilterRules(jpqlFilters);
        applyFilterRules(entityFilters);
        entities.forEach(entity -> getAclTemplate().checkPermission(entity, BasePermission.READ));
        return entities;
    }

    protected void applyFilterRules(List<? extends WebExtension<? super E>> filters){
        for (WebExtension<? super E> filter : filters) {
            for (FilterRule rule : filterRules) {
                if (rule.getClazz().equals(filter.getClass())){
                    rule.apply(filter);
                }
            }
        }
    }


    @Override
    public void deleteById(Id id) throws EntityNotFoundException {
        Optional<E> entity = super.findById(id);
        entity.ifPresent(e -> getAclTemplate().checkPermission(e, BasePermission.DELETE));
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
