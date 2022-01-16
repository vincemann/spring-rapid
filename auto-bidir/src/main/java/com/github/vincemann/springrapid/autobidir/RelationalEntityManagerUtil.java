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
    public Collection<IdentifiableEntity> findAllBiDirParents(IdentifiableEntity child);
    public Collection<IdentifiableEntity> findAllBiDirParents(IdentifiableEntity child, String... membersToCheck);

    public void linkBiDirParent(IdentifiableEntity child, IdentifiableEntity parentToSet);
    public void unlinkBiDirParent(IdentifiableEntity child, IdentifiableEntity parentToDelete);
    public void unlinkBiDirParents(IdentifiableEntity child);
    public void unlinkParentsChild(IdentifiableEntity child);
    public void linkChildrensParent(IdentifiableEntity biDirParent);


    public Map<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> findBiDirChildCollections(IdentifiableEntity parent);
    public Set<IdentifiableEntity> findSingleBiDirChildren(IdentifiableEntity parent);
    public Collection<IdentifiableEntity> findAllBiDirChildren(IdentifiableEntity parent);
    public Collection<IdentifiableEntity> findAllBiDirChildren(IdentifiableEntity parent, String... membersToCheck);

    public void linkBiDirChild(IdentifiableEntity parent, IdentifiableEntity newChild);
    public void unlinkBiDirChild(IdentifiableEntity parent, IdentifiableEntity biDirChildToRemove);
    public void unlinkChildrensParent(IdentifiableEntity parent);
    public void linkParentsChild(IdentifiableEntity biDirChild);


    public Map<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> findUniDirChildCollections(IdentifiableEntity parent);
    public Set<IdentifiableEntity> findSingleUniDirChildren(IdentifiableEntity parent);
    public Collection<IdentifiableEntity> findAllUniDirChildren(IdentifiableEntity child);
    public Collection<IdentifiableEntity> findAllUniDirChildren(IdentifiableEntity child, String... membersToCheck);

    public void linkUniDirChild(IdentifiableEntity parent,IdentifiableEntity newChild);
    public void unlinkUniDirChild(IdentifiableEntity parent, IdentifiableEntity toRemove);
}
