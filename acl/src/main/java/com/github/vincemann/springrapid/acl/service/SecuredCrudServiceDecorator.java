package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.core.GenericTypeResolver;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
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
                S extends CrudService<E, Id,Dto>,
                E extends IdAwareEntity<Id>,
                Id extends Serializable,
                Dto
                >
        extends SecuredServiceDecorator<S>
        implements CrudService<E,Id,Dto>
{

    private Class<E> entityClass;

    public SecuredCrudServiceDecorator(S decorated) {
        super(decorated);
        this.entityClass = (Class<E>) GenericTypeResolver.resolveTypeArguments(this.getClass(),SecuredCrudServiceDecorator.class)[1];
    }

    @Transactional
    @Override
    public E create(Dto dto) {
        return getDecorated().create(dto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<E> findAllById(Set<Id> ids) {
        List<E> entities = getDecorated().findAllById(ids);
        entities.forEach(e -> getAclTemplate().checkPermission(e,BasePermission.READ));
        return entities;
    }

    @Transactional
    @Override
    public void delete(Id id) {
        getAclTemplate().checkPermission(id,entityClass,BasePermission.DELETE);
        getDecorated().delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<E> findById(Id id) {
        Optional<E> entity = getDecorated().findById(id);
        entity.ifPresent(e -> getAclTemplate().checkPermission(e, BasePermission.READ));
        return entity;
    }
}
