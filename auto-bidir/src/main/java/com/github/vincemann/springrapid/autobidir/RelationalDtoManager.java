package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.autobidir.dto.RelationalDtoType;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RelationalDtoManager {

    public Set<RelationalDtoType> inferTypes(Class<?> entityClass);
    public Map<Class<IdentifiableEntity>, Serializable> findUniDirChildIds(IdentifiableEntity parent);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findUniDirChildIdCollections(IdentifiableEntity parent);
    public void addUniDirChildId(IdentifiableEntity child, IdentifiableEntity parent);


    public Map<Class<IdentifiableEntity>, Serializable> findBiDirChildIds(IdentifiableEntity parent);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findBiDirChildIdCollections(IdentifiableEntity parent);
    public void addBiDirChildId(IdentifiableEntity child, IdentifiableEntity parent);
    public Map<Class<IdentifiableEntity>, Serializable> findBiDirParentIds(IdentifiableEntity child);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findBiDirParentIdCollections(IdentifiableEntity child);
    public void addBiDirParentId(IdentifiableEntity parent, IdentifiableEntity child);

}
