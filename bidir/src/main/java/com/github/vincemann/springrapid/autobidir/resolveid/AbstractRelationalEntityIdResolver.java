package com.github.vincemann.springrapid.autobidir.resolveid;

import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManagerUtil;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.CrudServiceLocator;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Baseclass for components resolving entity <-> id, by scanning for:
 *  {@code BiDirChildId}, {@code BiDirParentId},
 *  {@code UniDirParentId}, {@code UniDirChildId}
 *  {@code UniDirChildIdCollection}, {@code BiDirChildIdCollection}
 */
public abstract class AbstractRelationalEntityIdResolver implements EntityIdResolver {

    private RelationalDtoManagerUtil relationalDtoManagerUtil;
    private RelationalEntityManagerUtil relationalEntityManagerUtil;
    private CrudServiceLocator crudServiceLocator;

    private RelationalDtoType supportedDtoType;

    public AbstractRelationalEntityIdResolver(RelationalDtoType supportedDtoType) {
        this.supportedDtoType = supportedDtoType;
    }

    @Override
    public boolean supports(Class<?> dtoClass) {
        Set<RelationalDtoType> relationalDtoTypes = relationalDtoManagerUtil.inferTypes(dtoClass);
        return relationalDtoTypes.contains(supportedDtoType);
    }

    // could be replaced by EntityLocator in the future
    protected IdentifiableEntity findEntityFromService(Class<IdentifiableEntity> entityClass, Serializable id) throws EntityNotFoundException, BadEntityException {
        CrudService service = crudServiceLocator.find(entityClass);
        Assert.notNull(service,"No Service found for entityClass: " + entityClass.getSimpleName());
        Optional<IdentifiableEntity> entity;
        try {
            entity = service.findById(id);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("ParentId: " + id + " was of wrong type for Service: " + service, e);
        }
        VerifyEntity.isPresent(entity, "No Parent of Type: " +entityClass.getSimpleName() + " found with id: " + id);
        return entity.get();
    }

    @Autowired
    public void setRelationalDtoManagerUtil(RelationalDtoManagerUtil relationalDtoManagerUtil) {
        this.relationalDtoManagerUtil = relationalDtoManagerUtil;
    }

    @Autowired
    public void setRelationalEntityManagerUtil(RelationalEntityManagerUtil relationalEntityManagerUtil) {
        this.relationalEntityManagerUtil = relationalEntityManagerUtil;
    }

    @Autowired
    public void setCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }

    public RelationalDtoManagerUtil getRelationalDtoManagerUtil() {
        return relationalDtoManagerUtil;
    }

    public RelationalEntityManagerUtil getRelationalEntityManagerUtil() {
        return relationalEntityManagerUtil;
    }

    public CrudServiceLocator getCrudServiceLocator() {
        return crudServiceLocator;
    }

    public RelationalDtoType getSupportedDtoType() {
        return supportedDtoType;
    }
}
