package com.github.vincemann.springrapid.autobidir.entity;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.github.vincemann.springrapid.core.util.HibernateProxyUtils.getTargetClass;

/**
 * needs to be executed within transaction
 */
@Transactional(propagation = Propagation.MANDATORY)
public class RelationalEntityManagerImpl implements RelationalEntityManager {

    private final Log log = LogFactory.getLog(RelationalEntityManagerImpl.class);

    private RelationalEntityManagerUtil helper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public <E extends IdAwareEntity> E create(E entity, String... membersToCheck) {
        Set<RelationalEntityType> relationalEntityTypes = helper.inferTypes(getTargetClass(entity));
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            if (log.isDebugEnabled())
                log.debug("applying pre persist BiDirParent logic for: " + entity);
            // also filter for class obj stored in annotation, so if I update only one BiDirChildCollection, only init this one
            // with the right class
            helper.linkBiDirChildrensParent(entity);
        }

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            if (log.isDebugEnabled())
                log.debug("applying pre persist BiDirChild logic for: " + entity);
            helper.linkBiDirParentsChild(entity);
        }
        return entity;
    }

    @Override
    public void delete(IdAwareEntity entity, String... membersToCheck) throws EntityNotFoundException, BadEntityException {
        Set<RelationalEntityType> relationalEntityTypes = helper.inferTypes(getTargetClass(entity));

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            if (log.isDebugEnabled())
                log.debug("applying pre remove BiDirParent logic for: " + entity.getClass());
            helper.unlinkBiDirChildrensParent(entity);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            if (log.isDebugEnabled())
                log.debug("applying pre remove BiDirChild logic for: " + entity);
            helper.unlinkBiDirParentsChild(entity);
        }
    }


    // todo infer membersToCheck cached again in RelationalServiceUpdateAdvice from single source and pass down this method
    @Override
    public <E extends IdAwareEntity> E partialUpdate(E managed, E oldEntity, E updateEntity, String... membersToCheck) throws EntityNotFoundException, BadEntityException {
        // only operate on non null fields of partialUpdateEntity
        Set<RelationalEntityType> relationalEntityTypes = helper.inferTypes(getTargetClass(oldEntity));
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            if (log.isDebugEnabled())
                log.debug("applying pre partial-update BiDirParent logic for: " + oldEntity.getClass());
            updateBiDirParentRelations(managed, oldEntity, updateEntity,membersToCheck);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            if (log.isDebugEnabled())
                log.debug("applying pre partial-update BiDirChild logic for: " + oldEntity.getClass());
            updateBiDirChildRelations(managed, oldEntity, updateEntity,membersToCheck);
        }
        return oldEntity;
    }


    @Override
    public <E extends IdAwareEntity> E fullUpdate(E managed, E oldEntity, E updateEntity, String... membersToCheck) throws EntityNotFoundException, BadEntityException {
        Set<RelationalEntityType> relationalEntityTypes = helper.inferTypes(getTargetClass(updateEntity));

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            if (log.isDebugEnabled())
                log.debug("applying pre full-update BiDirParent logic for: " + updateEntity.getClass());
            updateBiDirParentRelations(managed, oldEntity, updateEntity,membersToCheck);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            if (log.isDebugEnabled())
                log.debug("applying pre full-update BiDirChild logic for: " + updateEntity.getClass());
            updateBiDirChildRelations(managed, oldEntity, updateEntity, membersToCheck);
        }

        return updateEntity;
    }

    public Collection<IdAwareEntity> updateBiDirChildRelations(IdAwareEntity managed, IdAwareEntity oldChild, IdAwareEntity update, String... membersToCheck) throws BadEntityException, EntityNotFoundException {

        // old parents are detached
        Collection<IdAwareEntity> oldParents = helper.findAllBiDirParents(oldChild,membersToCheck);
        // new parents are detached
        Collection<IdAwareEntity> newParents = helper.findAllBiDirParents(update, membersToCheck);

        // find parents to unlink -> child of those need to be unlinked
        List<IdAwareEntity> removedParents = new ArrayList<>();
        for (IdAwareEntity oldParent : oldParents) {
            if (!newParents.contains(oldParent)) {
                removedParents.add(oldParent);
            }
        }

        // find added parents -> child of those need to be linked
        List<IdAwareEntity> addedParents = new ArrayList<>();
        for (IdAwareEntity newParent : newParents) {
            if (!oldParents.contains(newParent)) {
                addedParents.add(newParent);
            }
        }

        // both collections of children are detached, so relevant entities need to be merged after link/unlink
        adjustUpdatedEntities(addedParents, removedParents);

        // unlink child from removed parent -> parent.child = null;
        for (IdAwareEntity removedParent : removedParents) {
            if (log.isDebugEnabled())
                log.debug("update: unlinking child: " + update + " from removed parent: " + removedParent);
            // could use managed instead of update here but doesnt matter
            helper.unlinkBiDirChild(removedParent, update);
            entityManager.merge(removedParent);
        }

        // link child to added parent -> parent.child = child;
        for (IdAwareEntity addedParent : addedParents) {
            if (log.isDebugEnabled())
                log.debug("update: linking child: " + managed + " to added parent: " + addedParent);
            // cant use partial update entity here, need full managed updated
            helper.linkBiDirChild(addedParent, managed);
            entityManager.merge(addedParent);
        }
        return newParents;
    }

    public Collection<IdAwareEntity> updateBiDirParentRelations(IdAwareEntity managed, IdAwareEntity oldParent, IdAwareEntity updateParent, String... membersToCheck) throws BadEntityException, EntityNotFoundException {

        // old children are detached
        Collection<IdAwareEntity> oldChildren = helper.findAllBiDirChildren(oldParent,membersToCheck);
        // newChildren are detached
        Collection<IdAwareEntity> newChildren = helper.findAllBiDirChildren(updateParent,membersToCheck);

        // find removed children -> parent of those need to be unlinked
        List<IdAwareEntity> removedChildren = new ArrayList<>();
        for (IdAwareEntity oldChild : oldChildren) {
            if (!newChildren.contains(oldChild)) {
                removedChildren.add(oldChild);
            }
        }

        // find added children -> parent of those need to be linked
        List<IdAwareEntity> addedChildren = new ArrayList<>();
        for (IdAwareEntity newChild : newChildren) {
            if (!oldChildren.contains(newChild)) {
                addedChildren.add(newChild);
            }
        }

        // both collections of children are detached, so relevant entities need to be merged after link/unlink
        adjustUpdatedEntities(addedChildren, removedChildren);


        // unlink parent from removed child -> child.parent = null;
        for (IdAwareEntity removedChild : removedChildren) {
            if (log.isDebugEnabled())
                log.debug("unlinking parent: " + updateParent + " from removed child: " + removedChild);
            // could use managed instead of update here but doesnt matter
            helper.unlinkBiDirParent(removedChild, updateParent);
            entityManager.merge(removedChild);
        }

        //link parent to added child -> child.parent = parent;
        for (IdAwareEntity addedChild : addedChildren) {
            if (log.isDebugEnabled())
                log.debug("linking parent: " + managed + " to added child: " + addedChild);
            // need to set managed here not partial update variant with tons of null fields
            helper.linkBiDirParent(addedChild, managed);
            entityManager.merge(addedChild);
        }
        return newChildren;
    }


    protected <E> void adjustUpdatedEntities(List<E> added, List<E> removed) {
        removed.removeAll(added);
        added.removeAll(removed);
    }


    @Autowired
    public void setRelationalEntityManagerUtil(RelationalEntityManagerUtil helper) {
        this.helper = helper;
    }

}
