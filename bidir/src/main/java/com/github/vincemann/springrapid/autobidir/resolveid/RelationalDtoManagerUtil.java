package com.github.vincemann.springrapid.autobidir.resolveid;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.util.EntityReflectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RelationalDtoManagerUtil {

    public Set<RelationalDtoType> inferTypes(Class<?> dtoClass);
    public Map<Class<IdentifiableEntity>, Serializable> findUniDirChildIds(Object parent);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findUniDirChildIdCollections(Object parent, String... fieldsToCheck);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findAllUniDirChildIds(Object parent, String... fieldsToCheck);
    public void addUniDirChildId(IdentifiableEntity child, Object parent);

    public Map<Class<IdentifiableEntity>, Serializable> findBiDirChildIds(Object parent);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findBiDirChildIdCollections(Object parent,String... fieldsToCheck);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findAllBiDirChildIds(Object parent, String... fieldsToCheck);
    public void addBiDirChildId(IdentifiableEntity child, Object parent);

    public Map<Class<IdentifiableEntity>, Serializable> findBiDirParentIds(Object child);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findBiDirParentIdCollections(Object child,String... fieldsToCheck);
    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findAllBiDirParentIds(Object parent, String... fieldsToCheck);
    public void addBiDirParentId(IdentifiableEntity parent, Object child);

}
