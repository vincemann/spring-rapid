package com.github.vincemann.springrapid.autobidir.id;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.autobidir.id.RelationalDtoType;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RelationalDtoManager {

    public Set<RelationalDtoType> inferTypes(Class<?> entityClass);
    public Map<Class<IdentifiableEntity>, Serializable> findUniDirChildIds(Object parent);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findUniDirChildIdCollections(Object parent);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findAllUniDirChildIds(Object parent);
    public void addUniDirChildId(IdentifiableEntity child, Object parent);

    public Map<Class<IdentifiableEntity>, Serializable> findBiDirChildIds(Object parent);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findBiDirChildIdCollections(Object parent);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findAllBiDirChildIds(Object parent);
    public void addBiDirChildId(IdentifiableEntity child, Object parent);

    public Map<Class<IdentifiableEntity>, Serializable> findBiDirParentIds(Object child);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findBiDirParentIdCollections(Object child);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findAllBiDirParentIds(Object parent);
    public void addBiDirParentId(IdentifiableEntity parent, Object child);

}
