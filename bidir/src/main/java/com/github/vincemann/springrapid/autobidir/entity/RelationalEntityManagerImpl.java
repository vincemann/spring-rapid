package com.github.vincemann.springrapid.autobidir.entity;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static com.github.vincemann.springrapid.core.util.HibernateProxyUtils.getTargetClass;

/**
 * needs to be executed within transaction
 */
@Transactional(propagation = Propagation.MANDATORY)
public class RelationalEntityManagerImpl implements RelationalEntityManager {

    private final Log log = LogFactory.getLog(getClass());

    private RelationalEntityManagerUtil helper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public <E extends IdentifiableEntity> E create(E entity, String... membersToCheck) {
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
    public void delete(IdentifiableEntity entity, String... membersToCheck) throws EntityNotFoundException, BadEntityException {
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
    public <E extends IdentifiableEntity> E partialUpdate(E managed, E oldEntity, E updateEntity, String... membersToCheck) throws EntityNotFoundException, BadEntityException {
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
    public <E extends IdentifiableEntity> E fullUpdate(E managed, E oldEntity, E updateEntity, String... membersToCheck) throws EntityNotFoundException, BadEntityException {
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

    public Collection<IdentifiableEntity> updateBiDirChildRelations(IdentifiableEntity managed, IdentifiableEntity oldChild, IdentifiableEntity update, String... membersToCheck) throws BadEntityException, EntityNotFoundException {

        // old parents are detached
        Collection<IdentifiableEntity> oldParents = helper.findAllBiDirParents(oldChild,membersToCheck);
        // new parents are detached
        Collection<IdentifiableEntity> newParents = helper.findAllBiDirParents(update, membersToCheck);

        // find parents to unlink -> child of those need to be unlinked
        List<IdentifiableEntity> removedParents = new ArrayList<>();
        for (IdentifiableEntity oldParent : oldParents) {
            if (!newParents.contains(oldParent)) {
                removedParents.add(oldParent);
            }
        }

        // find added parents -> child of those need to be linked
        List<IdentifiableEntity> addedParents = new ArrayList<>();
        for (IdentifiableEntity newParent : newParents) {
            if (!oldParents.contains(newParent)) {
                addedParents.add(newParent);
            }
        }

        // both collections of children are detached, so relevant entities need to be merged after link/unlink
        adjustUpdatedEntities(addedParents, removedParents);

        // unlink child from removed parent -> parent.child = null;
        for (IdentifiableEntity removedParent : removedParents) {
            if (log.isDebugEnabled())
                log.debug("update: unlinking child: " + update + " from removed parent: " + removedParent);
            // could use managed instead of update here but doesnt matter
            helper.unlinkBiDirChild(removedParent, update);
            entityManager.merge(removedParent);
        }

        // link child to added parent -> parent.child = child;
        for (IdentifiableEntity addedParent : addedParents) {
            if (log.isDebugEnabled())
                log.debug("update: linking child: " + managed + " to added parent: " + addedParent);
            // cant use partial update entity here, need full managed updated
            helper.linkBiDirChild(addedParent, managed);
            entityManager.merge(addedParent);
        }
        return newParents;
    }

    public Collection<IdentifiableEntity> updateBiDirParentRelations(IdentifiableEntity managed, IdentifiableEntity oldParent, IdentifiableEntity updateParent, String... membersToCheck) throws BadEntityException, EntityNotFoundException {

        // old children are detached
        Collection<IdentifiableEntity> oldChildren = helper.findAllBiDirChildren(oldParent,membersToCheck);
        // newChildren are detached
        Collection<IdentifiableEntity> newChildren = helper.findAllBiDirChildren(updateParent,membersToCheck);

        // find removed children -> parent of those need to be unlinked
        List<IdentifiableEntity> removedChildren = new ArrayList<>();
        for (IdentifiableEntity oldChild : oldChildren) {
            if (!newChildren.contains(oldChild)) {
                removedChildren.add(oldChild);
            }
        }

        // find added children -> parent of those need to be linked
        List<IdentifiableEntity> addedChildren = new ArrayList<>();
        for (IdentifiableEntity newChild : newChildren) {
            if (!oldChildren.contains(newChild)) {
                addedChildren.add(newChild);
            }
        }

        // both collections of children are detached, so relevant entities need to be merged after link/unlink
        adjustUpdatedEntities(addedChildren, removedChildren);


        // unlink parent from removed child -> child.parent = null;
        for (IdentifiableEntity removedChild : removedChildren) {
            if (log.isDebugEnabled())
                log.debug("unlinking parent: " + updateParent + " from removed child: " + removedChild);
            // could use managed instead of update here but doesnt matter
            helper.unlinkBiDirParent(removedChild, updateParent);
            entityManager.merge(removedChild);
        }

        //link parent to added child -> child.parent = parent;
        for (IdentifiableEntity addedChild : addedChildren) {
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
