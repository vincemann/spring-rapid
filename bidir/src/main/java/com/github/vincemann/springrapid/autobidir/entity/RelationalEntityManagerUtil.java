package com.github.vincemann.springrapid.autobidir.entity;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Low level support class, used by high level components like {@link RelationalEntityManager}.
 * Its best to stick with annotations like {@link com.github.vincemann.springrapid.autobidir.EnableAutoBiDir} or, if needed, use
 * {@link RelationalEntityManager} instead of this component.
 */
public interface RelationalEntityManagerUtil {

    Set<RelationalEntityType> inferTypes(Class<? extends IdAwareEntity> entityClass);

    /**
     parent of child is set.
     child.parent = parent;
     */
    public void linkBiDirParent(IdAwareEntity child, IdAwareEntity parentToSet, String... membersToCheck);

    /**
     child of parent is set.
     parent.child = child;
     */
    public void linkBiDirChild(IdAwareEntity parent, IdAwareEntity childToSet, String... membersToCheck);

    /**
     unset parent of child
     child.parent = null;
     */
    public void unlinkBiDirParent(IdAwareEntity child, IdAwareEntity parentToDelete, String... membersToCheck);


    /**
     unset child of parent
     parent.child = null;
     */
    public void unlinkBiDirChild(IdAwareEntity parent, IdAwareEntity childToDelete, String... membersToCheck);




    public void linkUniDirChild(IdAwareEntity parent, IdAwareEntity newChild, String... membersToCheck);

    public void unlinkUniDirChild(IdAwareEntity parent, IdAwareEntity toRemove, String... membersToCheck);

    /**
     * find all bidir parents of given child and unlink *child from them*
     *
     * for parent in child.getParents:
     *      parent.child = null;
     * done
     */
    public void unlinkBiDirParentsFrom(IdAwareEntity child, String... membersToCheck);

    /**
     * find all bidir children of given parent and unlink *parent from them*
     *
     * for child in parent.getChildren:
     *      child.parent = null;
     * done
     */
    public void unlinkBiDirChildrensParent(IdAwareEntity parent, String... membersToCheck);


    public void unlinkBiDirParentsChild(IdAwareEntity child, String... membersToCheck);

    public void linkBiDirChildrensParent(IdAwareEntity parent, String... membersToCheck);

    /**
     * find all bidir parents of bidir
     * @param child and link it to them
     * -> set backreference of children
     */
    public void linkBiDirParentsChild(IdAwareEntity child, String... membersToCheck);






    public Map<Class<IdAwareEntity>,Collection<IdAwareEntity>> findBiDirParentCollections(IdAwareEntity child, String... membersToCheck);
    public Collection<IdAwareEntity> findSingleBiDirParents(IdAwareEntity child, String... membersToCheck);
    public Collection<IdAwareEntity> findAllBiDirParents(IdAwareEntity child, String... membersToCheck);


    public Map<Class<IdAwareEntity>,Collection<IdAwareEntity>> findBiDirChildCollections(IdAwareEntity parent, String... membersToCheck);
    public Set<IdAwareEntity> findSingleBiDirChildren(IdAwareEntity parent, String... membersToCheck);
    public Collection<IdAwareEntity> findAllBiDirChildren(IdAwareEntity parent, String... membersToCheck);


    public Map<Class<IdAwareEntity>,Collection<IdAwareEntity>> findUniDirChildCollections(IdAwareEntity parent, String... membersToCheck);
    public Set<IdAwareEntity> findSingleUniDirChildren(IdAwareEntity parent, String... membersToCheck);
    public Collection<IdAwareEntity> findAllUniDirChildren(IdAwareEntity child, String... membersToCheck);


}
