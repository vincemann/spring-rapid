package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RelationalEntityManagerUtil {

    Set<RelationalEntityType> inferTypes(Class<? extends IdentifiableEntity> entityClass);
    public Map<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> findBiDirParentCollections(IdentifiableEntity child);
    public Collection<IdentifiableEntity> findSingleBiDirParents(IdentifiableEntity child);
    public void linkBiDirParent(IdentifiableEntity child, IdentifiableEntity parentToSet);
    public void unlinkBiDirParents(IdentifiableEntity child);
    public void unlinkBiDirParent(IdentifiableEntity child, IdentifiableEntity parentToDelete);
    public void unlinkParentsChildren(IdentifiableEntity child);


    public Map<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> findBiDirChildCollections(IdentifiableEntity parent);
    public Set<IdentifiableEntity> findSingleBiDirChildren(IdentifiableEntity parent);
    public void linkBiDirChild(IdentifiableEntity parent, IdentifiableEntity newChild);
    public void unlinkBiDirChild(IdentifiableEntity parent, IdentifiableEntity biDirChildToRemove);
    public void unlinkChildrensParent(IdentifiableEntity parent);


    public Map<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> findUniDirChildCollections(IdentifiableEntity parent);
    public Set<IdentifiableEntity> findSingleUniDirChildren(IdentifiableEntity parent);
    public void linkUniDirChild(IdentifiableEntity parent,IdentifiableEntity newChild);
    public void unlinkUniDirChild(IdentifiableEntity parent, IdentifiableEntity toRemove);
}
