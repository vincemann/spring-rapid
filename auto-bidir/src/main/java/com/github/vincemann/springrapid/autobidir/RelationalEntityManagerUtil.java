package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RelationalEntityManagerUtil {

    Set<RelationalEntityType> inferTypes(Class<? extends IdentifiableEntity> entityClass);

    /**
     parent of child is set.
     child.parent = parent;
     */
    public void linkBiDirParent(IdentifiableEntity child, IdentifiableEntity parentToSet, String... membersToCheck);

    /**
     child of parent is set.
     parent.child = child;
     */
    public void linkBiDirChild(IdentifiableEntity parent, IdentifiableEntity childToSet, String... membersToCheck);

    /**
     unset parent of child
     child.parent = null;
     */
    public void unlinkBiDirParent(IdentifiableEntity child, IdentifiableEntity parentToDelete, String... membersToCheck);


    /**
     unset child of parent
     parent.child = null;
     */
    public void unlinkBiDirChild(IdentifiableEntity parent, IdentifiableEntity childToDelete, String... membersToCheck);




    public void linkUniDirChild(IdentifiableEntity parent,IdentifiableEntity newChild, String... membersToCheck);

    public void unlinkUniDirChild(IdentifiableEntity parent, IdentifiableEntity toRemove, String... membersToCheck);

    /**
     * find all bidir parents of given child and unlink *child from them*
     *
     * for parent in child.getParents:
     *      parent.child = null;
     * done
     */
    public void unlinkBiDirParentsFrom(IdentifiableEntity child, String... membersToCheck);

    /**
     * find all bidir children of given parent and unlink *parent from them*
     *
     * for child in parent.getChildren:
     *      child.parent = null;
     * done
     */
    public void unlinkBiDirChildrensParent(IdentifiableEntity parent, String... membersToCheck);


    public void unlinkBiDirParentsChild(IdentifiableEntity child, String... membersToCheck);

    public void linkBiDirChildrensParent(IdentifiableEntity parent, String... membersToCheck);

    /**
     * find all bidir parents of bidir
     * @param child and link it to them
     * -> set backreference of children
     */
    public void linkBiDirParentsChild(IdentifiableEntity child, String... membersToCheck);






    public Map<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> findBiDirParentCollections(IdentifiableEntity child, String... membersToCheck);
    public Collection<IdentifiableEntity> findSingleBiDirParents(IdentifiableEntity child, String... membersToCheck);
    public Collection<IdentifiableEntity> findAllBiDirParents(IdentifiableEntity child, String... membersToCheck);


    public Map<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> findBiDirChildCollections(IdentifiableEntity parent, String... membersToCheck);
    public Set<IdentifiableEntity> findSingleBiDirChildren(IdentifiableEntity parent, String... membersToCheck);
    public Collection<IdentifiableEntity> findAllBiDirChildren(IdentifiableEntity parent, String... membersToCheck);


    public Map<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> findUniDirChildCollections(IdentifiableEntity parent, String... membersToCheck);
    public Set<IdentifiableEntity> findSingleUniDirChildren(IdentifiableEntity parent, String... membersToCheck);
    public Collection<IdentifiableEntity> findAllUniDirChildren(IdentifiableEntity child, String... membersToCheck);


}
