package com.github.vincemann.springrapid.autobidir.resolveid;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RelationalDtoManagerUtil {

    public Set<RelationalDtoType> inferTypes(Class<?> dtoClass);
    public Map<Class<IdAwareEntity>, Serializable> findUniDirChildIds(Object parent);
    public Map<Class<IdAwareEntity>, Collection<Serializable>> findUniDirChildIdCollections(Object parent, String... fieldsToCheck);
    public Map<Class<IdAwareEntity>, Collection<Serializable>> findAllUniDirChildIds(Object parent, String... fieldsToCheck);
    public void addUniDirChildId(IdAwareEntity child, Object parent);

    public Map<Class<IdAwareEntity>, Serializable> findBiDirChildIds(Object parent);
    public Map<Class<IdAwareEntity>, Collection<Serializable>> findBiDirChildIdCollections(Object parent, String... fieldsToCheck);
    public Map<Class<IdAwareEntity>, Collection<Serializable>> findAllBiDirChildIds(Object parent, String... fieldsToCheck);
    public void addBiDirChildId(IdAwareEntity child, Object parent);

    public Map<Class<IdAwareEntity>, Serializable> findBiDirParentIds(Object child);
    public Map<Class<IdAwareEntity>, Collection<Serializable>> findBiDirParentIdCollections(Object child, String... fieldsToCheck);
    public Map<Class<IdAwareEntity>, Collection<Serializable>> findAllBiDirParentIds(Object parent, String... fieldsToCheck);
    public void addBiDirParentId(IdAwareEntity parent, Object child);

}
