package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RelationalEntityManagerUtil {

    Set<RelationalEntityType> inferTypes(Class<? extends IdentifiableEntity> entityClass);

    /**
     * link bidir
     * @param parentToSet
     * to explicit bidir
     * @param child
     */
    public void linkBiDirParent(IdentifiableEntity child, IdentifiableEntity parentToSet);

    /**
     * link bidir
     * @param childToSet
     * to explicit bidir
     * @param parent
     */
    public void linkBiDirChild(IdentifiableEntity parent, IdentifiableEntity childToSet);

    /**
     * unlink bidir
     * @param parentToDelete
     * from explicit bidir
     * @param child
     */
    public void unlinkBiDirParent(IdentifiableEntity child, IdentifiableEntity parentToDelete);


    /**
     * unlink bidir
     * @param childToDelete
     * from explicit bidir
     * @param parent
     */
    public void unlinkBiDirChild(IdentifiableEntity parent, IdentifiableEntity childToDelete);




    public void linkUniDirChild(IdentifiableEntity parent,IdentifiableEntity newChild);

    public void unlinkUniDirChild(IdentifiableEntity parent, IdentifiableEntity toRemove);

    /**
     * find all bidir parents of bidir
     * @param child and unlink *child from them*
     * -> remove references from child to parent, not backref
     */
    public void unlinkBiDirParentsFrom(IdentifiableEntity child);

    /**
     * Find all bidir children of bidir
     * @param parent and unlink it from them.
     * -> remove childrens backreference
     */
    public void unlinkBiDirChildrensParent(IdentifiableEntity parent);

    /**
     * Find all bidir parents of bidir
     * @param child and unlink it from them.
     * -> remove parents backreference
     */
    public void unlinkBiDirParentsChild(IdentifiableEntity child);
    /**
     * find all bidir children of bidir
     * @param parent and link it to them
     * -> set backreference of children
     */
    public void linkBiDirChildrensParent(IdentifiableEntity parent);

    /**
     * find all bidir parents of bidir
     * @param child and link it to them
     * -> set backreference of children
     */
    public void linkBiDirParentsChild(IdentifiableEntity child);






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
