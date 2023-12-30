package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import java.util.*;

import static com.github.vincemann.springrapid.core.util.ProxyUtils.getTargetClass;

@Slf4j
@Transactional
public class RapidRelationalEntityManager implements RelationalEntityManager {

    private RelationalEntityManagerUtil relationalEntityManagerUtil;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public <E extends IdentifiableEntity> E save(E entity, String... membersToCheck) {
//        if (entity.getId() != null) {
//            throw new IllegalArgumentException("save needs null id");
//        }
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(getTargetClass(entity));
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            if (log.isDebugEnabled())
                log.debug("applying pre persist BiDirParent logic for: " + entity);
            // also filter for class obj stored in annotation, so if I update only one BiDirChildCollection, only init this one
            // with the right class
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirChildCollection.class);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirChildEntity.class);
            relationalEntityManagerUtil.linkBiDirChildrensParent(entity);
        }

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            if (log.isDebugEnabled())
                log.debug("applying pre persist BiDirChild logic for: " + entity);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirParentEntity.class);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirParentCollection.class);
            relationalEntityManagerUtil.linkBiDirParentsChild(entity);
        }
        return entity;
    }

    @Override
    public void remove(IdentifiableEntity entity, String... membersToCheck) throws EntityNotFoundException, BadEntityException {
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(getTargetClass(entity));

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            if (log.isDebugEnabled())
                log.debug("applying pre remove BiDirParent logic for: " + entity.getClass());
            relationalEntityManagerUtil.unlinkBiDirChildrensParent(entity);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            if (log.isDebugEnabled())
                log.debug("applying pre remove BiDirChild logic for: " + entity);
            relationalEntityManagerUtil.unlinkBiDirParentsChild(entity);
        }
    }


    // todo infer membersToCheck cached again in RelationalServiceUpdateAdvice from single source and pass down this method
    @Override
    public <E extends IdentifiableEntity> E partialUpdate(E oldEntity, E updateEntity, String... membersToCheck) throws EntityNotFoundException, BadEntityException {
        // only operate on non null fields of partialUpdateEntity
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(getTargetClass(oldEntity));
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            if (log.isDebugEnabled())
                log.debug("applying pre partial-update BiDirParent logic for: " + oldEntity.getClass());
            updateBiDirParentRelations(oldEntity, updateEntity,membersToCheck);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            if (log.isDebugEnabled())
                log.debug("applying pre partial-update BiDirChild logic for: " + oldEntity.getClass());
            updateBiDirChildRelations(oldEntity, updateEntity,membersToCheck);
        }
        // should be updated now, otherwise we need parameter updateEntity instead
        return oldEntity;
    }


    @Override
    public <E extends IdentifiableEntity> E update(E oldEntity, E updateEntity, String... membersToCheck) throws EntityNotFoundException, BadEntityException {
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(getTargetClass(updateEntity));

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            if (log.isDebugEnabled())
                log.debug("applying pre full-update BiDirParent logic for: " + updateEntity.getClass());
            updateBiDirParentRelations(oldEntity, updateEntity,membersToCheck);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            if (log.isDebugEnabled())
                log.debug("applying pre full-update BiDirChild logic for: " + updateEntity.getClass());
            updateBiDirChildRelations(oldEntity, updateEntity, membersToCheck);
        }

        return updateEntity;
    }

    public Collection<IdentifiableEntity> updateBiDirChildRelations(IdentifiableEntity oldChild, IdentifiableEntity child, String... membersToCheck) throws BadEntityException, EntityNotFoundException {

        Collection<IdentifiableEntity> oldParents = relationalEntityManagerUtil.findAllBiDirParents(oldChild,membersToCheck);
        Collection<IdentifiableEntity> newParents = relationalEntityManagerUtil.findAllBiDirParents(child, membersToCheck);

        //find parents to unlink
        List<IdentifiableEntity> removedParents = new ArrayList<>();
        for (IdentifiableEntity oldParent : oldParents) {
            if (!newParents.contains(oldParent)) {
                removedParents.add(oldParent);
            }
        }

        //find added parents
        List<IdentifiableEntity> addedParents = new ArrayList<>();
        for (IdentifiableEntity newParent : newParents) {
            if (!oldParents.contains(newParent)) {
                addedParents.add(newParent);
            }else {
                // all parents need to be merged, non added children can be merged here already
                entityManager.merge(newParent);
            }
        }

        adjustUpdatedEntities(addedParents, removedParents);

        // unlink Child from certain Parents
        for (IdentifiableEntity removedParent : removedParents) {
            if (log.isDebugEnabled())
                log.debug("update: unlinking parent: " + removedParent + " from child: " + child);
//            relationalEntityManagerUtil.unlinkBiDirChild(removedParent, oldChild);
            relationalEntityManagerUtil.unlinkBiDirChild(removedParent, child, membersToCheck);  // somehow does not make a difference but makes more sense like that imo
        }

        // link added Parent to child
        for (IdentifiableEntity addedParent : addedParents) {
            if (log.isDebugEnabled())
                log.debug("update: linking parent: " + addedParent + " to child: " + child);
            relationalEntityManagerUtil.linkBiDirChild(addedParent, child, membersToCheck);
            // new parents may be detached, so merge them, must happen after linking!
            entityManager.merge(addedParent);
        }
//        entityManager.merge(child); wont do no harm, maybe needed if child is detached?
        return newParents;
    }

    public Collection<IdentifiableEntity> updateBiDirParentRelations(IdentifiableEntity oldParent, IdentifiableEntity updateParent, String... membersToCheck) throws BadEntityException, EntityNotFoundException {

        Collection<IdentifiableEntity> oldChildren = relationalEntityManagerUtil.findAllBiDirChildren(oldParent,membersToCheck);
        Collection<IdentifiableEntity> newChildren = relationalEntityManagerUtil.findAllBiDirChildren(updateParent,membersToCheck);

        //find Children to unlink
        List<IdentifiableEntity> removedChildren = new ArrayList<>();
        for (IdentifiableEntity oldChild : oldChildren) {
            if (!newChildren.contains(oldChild)) {
                removedChildren.add(oldChild);
            }
        }

        //find added Children
        List<IdentifiableEntity> addedChildren = new ArrayList<>();
        for (IdentifiableEntity newChild : newChildren) {
            if (!oldChildren.contains(newChild)) {
                addedChildren.add(newChild);
            }else {
                // all children need to be merged, non added children can be merged here already
                entityManager.merge(newChild);
            }
        }

        adjustUpdatedEntities(addedChildren, removedChildren);

        //unlink removed Children from parent
        for (IdentifiableEntity removedChild : removedChildren) {
            if (log.isDebugEnabled())
                log.debug("unlinking child: " + removedChild + " from parent: " + updateParent);
//            relationalEntityManagerUtil.unlinkBiDirParent(removedChild, oldParent);
            relationalEntityManagerUtil.unlinkBiDirParent(removedChild, updateParent); // somehow does not make a difference but makes more sense like that imo
        }

        //link added Children to parent
        for (IdentifiableEntity addedChild : addedChildren) {
            if (log.isDebugEnabled())
                log.debug("linking child: " + addedChild + " to parent: " + updateParent);
            // illness gets set of pets updated, illness = child
            relationalEntityManagerUtil.linkBiDirParent(addedChild, updateParent);
            // new children may be detached, so merge them , must happen after linking!
            entityManager.merge(addedChild);
        }
        return newChildren;
//        entityManager.merge(parent); wont do no harm, maybe needed if newChild is detached?
    }

//    public Collection<IdentifiableEntity> partialUpdateBiDirParentRelations(IdentifiableEntity oldParent, IdentifiableEntity partialUpdateParent, String... membersToCheck) throws BadEntityException, EntityNotFoundException {
//
//        Collection<IdentifiableEntity> oldChildren = relationalEntityManagerUtil.findAllBiDirChildren(oldParent,membersToCheck);
//        Collection<IdentifiableEntity> newChildren = relationalEntityManagerUtil.findAllBiDirChildren(partialUpdateParent,membersToCheck);
//
//        //find Children to unlink
//        List<IdentifiableEntity> removedChildren = new ArrayList<>();
//        for (IdentifiableEntity oldChild : oldChildren) {
//            if (!newChildren.contains(oldChild)) {
//                removedChildren.add(oldChild);
//            }
//        }
//
//        //find added Children
//        List<IdentifiableEntity> addedChildren = new ArrayList<>();
//        for (IdentifiableEntity newChild : newChildren) {
//            if (!oldChildren.contains(newChild)) {
//                addedChildren.add(newChild);
//            }else {
//                // all children need to be merged, non added children can be merged here already
//                entityManager.merge(newChild);
//            }
//        }
//
//        adjustUpdatedEntities(addedChildren, removedChildren);
//
//        //unlink removed Children from updateParent
//        for (IdentifiableEntity removedChild : removedChildren) {
//            if (log.isDebugEnabled()){
//                log.debug("update: unlinking child: " + removedChild+ " from parent: " + oldParent);
//                log.debug("update: unlinking parent: " + oldParent + " from child: " + removedChild);
//            }
////            relationalEntityManagerUtil.unlinkBiDirParent(removedChild, oldParent);
//            relationalEntityManagerUtil.unlinkBiDirChild(oldParent,removedChild,membersToCheck);
//            relationalEntityManagerUtil.unlinkBiDirParent(removedChild, oldParent); // somehow does not make a difference but makes more sense like that imo
//        }
//
//        //link added Children to updateParent
//        for (IdentifiableEntity addedChild : addedChildren) {
//            if (log.isDebugEnabled())
//                log.debug("update: linking child: " + addedChild + " to parent: " + oldParent);
//            // illness gets set of pets updated, illness = child
//            relationalEntityManagerUtil.linkBiDirChild(oldParent,addedChild,membersToCheck);
//            if (log.isDebugEnabled())
//                log.debug("update: linking parent: " + oldParent + " to child: " + addedChild);
//            relationalEntityManagerUtil.linkBiDirParent(addedChild, oldParent);
//            // new children may be detached, so merge them , must happen after linking!
//            entityManager.merge(addedChild);
//        }
//        return newChildren;
////        entityManager.merge(updateParent); wont do no harm, maybe needed if newChild is detached?
//    }
//
//    private Collection<IdentifiableEntity> partialUpdateBiDirChildRelations(IdentifiableEntity oldChild, IdentifiableEntity partialUpdateChild, String[] membersToCheck) {
//        Collection<IdentifiableEntity> oldParents = relationalEntityManagerUtil.findAllBiDirParents(oldChild,membersToCheck);
//        Collection<IdentifiableEntity> newParents = relationalEntityManagerUtil.findAllBiDirParents(partialUpdateChild, membersToCheck);
//
//        //find parents to unlink
//        List<IdentifiableEntity> removedParents = new ArrayList<>();
//        for (IdentifiableEntity oldParent : oldParents) {
//            if (!newParents.contains(oldParent)) {
//                removedParents.add(oldParent);
//            }
//        }
//
//        //find added parents
//        List<IdentifiableEntity> addedParents = new ArrayList<>();
//        for (IdentifiableEntity newParent : newParents) {
//            if (!oldParents.contains(newParent)) {
//                addedParents.add(newParent);
//            }else {
//                // all parents need to be merged, non added children can be merged here already
//                entityManager.merge(newParent);
//            }
//        }
//
//        adjustUpdatedEntities(addedParents, removedParents);
//
//        // unlink Child from certain Parents
//        for (IdentifiableEntity removedParent : removedParents) {
//            if (log.isDebugEnabled())
//                log.debug("update: unlinking parent: " + removedParent + " from child: " + oldChild);
////            relationalEntityManagerUtil.unlinkBiDirChild(removedParent, oldChild);
//            relationalEntityManagerUtil.unlinkBiDirParent(partialUpdateChild,removedParent);
//            if (log.isDebugEnabled())
//                log.debug("update: unlinking child: " + oldChild+ " from parent: " + removedParent);
//            relationalEntityManagerUtil.unlinkBiDirChild(removedParent, partialUpdateChild);  // somehow does not make a difference but makes more sense like that imo
//        }
//
//        // link added Parent to child
//        for (IdentifiableEntity addedParent : addedParents) {
//            if (log.isDebugEnabled())
//                log.debug("update: linking parent: " + addedParent + " to child: " + oldChild);
//            relationalEntityManagerUtil.linkBiDirParent(partialUpdateChild,addedParent);
//            if (log.isDebugEnabled())
//                log.debug("update: linking child: " + oldChild + " to parent: " + addedParent);
//            relationalEntityManagerUtil.linkBiDirChild(addedParent, partialUpdateChild);
//            // new parents may be detached, so merge them, must happen after linking!
//            entityManager.merge(addedParent);
//        }
////        entityManager.merge(child); wont do no harm, maybe needed if child is detached?
//        return newParents;
//    }


    protected <E> void adjustUpdatedEntities(List<E> added, List<E> removed) {
        removed.removeAll(added);
        added.removeAll(removed);
    }


//    protected void mergeChildrensParents(IdentifiableEntity biDirChild) {
//        for (IdentifiableEntity parent : relationalEntityManagerUtil.findAllBiDirParents(biDirChild)) {
//            entityManager.merge(parent);
//        }
//    }
//
//    protected void mergeParentsChildren(IdentifiableEntity biDirParent) {
//        for (IdentifiableEntity child : relationalEntityManagerUtil.findAllBiDirChildren(biDirParent)) {
//            entityManager.merge(child);
//        }
//    }
//
//    private void replaceParentsChildRefAndMerge(IdentifiableEntity child) {
//        //set backreferences
//
//        for (IdentifiableEntity parent : relationalEntityManagerUtil.findAllBiDirParents(child)) {
//            relationalEntityManagerUtil.linkBiDirChild(parent,child);
//            entityManager.merge(parent);
//        }
//    }
//
//    private void replaceChildrensParentRefAndMerge(IdentifiableEntity parent) {
//        //set backreferences
//        for (IdentifiableEntity child : relationalEntityManagerUtil.findAllBiDirChildren(parent)) {
//            relationalEntityManagerUtil.linkBiDirParent(child,parent);
//            entityManager.merge(child);
//        }
//    }

//    private void updateAddedChildren(IdentifiableEntity parent, List<IdentifiableEntity> addedChildren) {
////        for (IdentifiableEntity child : relationalEntityManagerUtil.findAllBiDirChildren(parent)) {
////            System.err.println(child);
////        }
//
//        //set backreferences
//        for (IdentifiableEntity child : addedChildren) {
//            System.err.println(child);
//            relationalEntityManagerUtil.linkBiDirParent(child,parent);
//            entityManager.merge(child);
//        }
//    }
//
//    private void updateAddedParents(IdentifiableEntity child, List<IdentifiableEntity> addedParents) {
//        //set backreferences
//        for (IdentifiableEntity parent : addedParents) {
//            System.err.println(parent);
//            relationalEntityManagerUtil.linkBiDirChild(parent,child);
//            entityManager.merge(parent);
//        }
//    }


//    private void linkChildrensParent(IdentifiableEntity biDirParent) {
//        Set<? extends IdentifiableEntity> children = relationalEntityManagerUtil.findSingleBiDirChildren(biDirParent);
//        for (IdentifiableEntity child : children) {
//            relationalEntityManagerUtil.linkBiDirParent(child, biDirParent);
//        }
//        Collection<Collection<IdentifiableEntity>> childCollections = relationalEntityManagerUtil.findBiDirChildCollections(biDirParent).values();
//        for (Collection<IdentifiableEntity> childCollection : childCollections) {
//            for (IdentifiableEntity child : childCollection) {
//                relationalEntityManagerUtil.linkBiDirParent(child, biDirParent);
//            }
//        }

//    }

//    private void linkParentsChild(IdentifiableEntity biDirChild) {
//        //set backreferences
//
//        Collection<Collection<IdentifiableEntity>> parentCollections = relationalEntityManagerUtil.findBiDirParentCollections(biDirChild).values();
//        for (Collection<IdentifiableEntity> parentCollection : parentCollections) {
//            for (IdentifiableEntity biDirParent : parentCollection) {
//                relationalEntityManagerUtil.linkBiDirChild(biDirParent,biDirChild);
//            }
//        }
//
//        for (IdentifiableEntity parent : relationalEntityManagerUtil.findSingleBiDirParents(biDirChild)) {
//            relationalEntityManagerUtil.linkBiDirChild(parent,biDirChild);
//        }
//
//    }


    @Autowired
    public void setRelationalEntityManagerUtil(RelationalEntityManagerUtil relationalEntityManagerUtil) {
        this.relationalEntityManagerUtil = relationalEntityManagerUtil;
    }

}
