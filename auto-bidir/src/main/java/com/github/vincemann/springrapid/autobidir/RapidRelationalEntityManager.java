package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Slf4j
@Transactional
public class RapidRelationalEntityManager implements RelationalEntityManager {

    private RelationalEntityManagerUtil relationalEntityManagerUtil;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public <E extends IdentifiableEntity> E save(E entity) {
        if (entity.getId() != null) {
            throw new IllegalArgumentException("save needs null id");
        }
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(entity.getClass());
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            log.debug("applying pre persist BiDirParent logic for: " + entity);
            // also filter for class obj stored in annotation, so if I update only one BiDirChildCollection, only init this one
            // with the right class
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirChildCollection.class);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirChildEntity.class);
            relationalEntityManagerUtil.linkChildrensParent(entity);

        }

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            log.debug("applying pre persist BiDirChild logic for: " + entity);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirParentEntity.class);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirParentCollection.class);
            relationalEntityManagerUtil.linkParentsChild(entity);
        }
        return entity;
    }

    @Override
    public void remove(IdentifiableEntity entity) throws EntityNotFoundException, BadEntityException {
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(entity.getClass());

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            log.debug("applying pre remove BiDirParent logic for: " + entity.getClass());
            relationalEntityManagerUtil.unlinkChildrensParent(entity);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            log.debug("applying pre remove BiDirChild logic for: " + entity);
            relationalEntityManagerUtil.unlinkParentsChild(entity);
        }
    }


    @Override
    public <E extends IdentifiableEntity> E partialUpdate(E oldEntity, E updateEntity, E partialUpdateEntity) throws EntityNotFoundException, BadEntityException {
        // only operate on non null fields of partialUpdateEntity
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(updateEntity.getClass());
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            log.debug("applying pre partial-update BiDirParent logic for: " + updateEntity.getClass());
            List<IdentifiableEntity> addedChildren = updateBiDirParentRelations(oldEntity, updateEntity);
            replaceChildrensParentRefAndMerge(updateEntity);
//            updateAddedChildren(updateEntity,addedChildren);
//            mergeParentsChildren(updateEntity);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            log.debug("applying pre partial-update BiDirChild logic for: " + updateEntity.getClass());
            List<IdentifiableEntity> addedParents = updateBiDirChildRelations(oldEntity, updateEntity);
            replaceParentsChildRefAndMerge(updateEntity);
//            updateAddedParents(updateEntity,addedParents);
//            mergeChildrensParents(updateEntity);
        }
        return updateEntity;
    }

    @Override
    public <E extends IdentifiableEntity> E update(E oldEntity, E updateEntity) throws EntityNotFoundException, BadEntityException {
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(updateEntity.getClass());

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            log.debug("applying pre full-update BiDirParent logic for: " + updateEntity.getClass());
            updateBiDirParentRelations(oldEntity, updateEntity);
            // todo er will die added children resp. added parents noch mal von der bereits gelinketen entity frisch haben
            // versuch an ein set der added parents/children zu kommen und nur über die zu iterieren
            replaceChildrensParentRefAndMerge(updateEntity);
//            mergeParentsChildren(updateEntity);

        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            log.debug("applying pre full-update BiDirChild logic for: " + updateEntity.getClass());
            updateBiDirChildRelations(oldEntity, updateEntity);
            replaceParentsChildRefAndMerge(updateEntity);
//            mergeChildrensParents(updateEntity);
        }

        return updateEntity;
    }

    public List<IdentifiableEntity> updateBiDirChildRelations(IdentifiableEntity oldChild, IdentifiableEntity child) throws BadEntityException, EntityNotFoundException {

        Collection<IdentifiableEntity> oldParents = relationalEntityManagerUtil.findAllBiDirParents(oldChild);
        Collection<IdentifiableEntity> newParents = relationalEntityManagerUtil.findAllBiDirParents(child);

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
            }
        }

        adjustUpdatedEntities(addedParents, removedParents);

        // unlink Child from certain Parents
        for (IdentifiableEntity removedParent : removedParents) {
            log.debug("update: unlinking parent: " + removedParent + " from child: " + child);
//            relationalEntityManagerUtil.unlinkBiDirChild(removedParent, oldChild);
            relationalEntityManagerUtil.unlinkBiDirChild(removedParent, child);  // somehow does not make a difference but makes more sense like that imo
        }

        // link added Parent to child
        for (IdentifiableEntity addedParent : addedParents) {
            log.debug("update: linking parent: " + addedParent + " to child: " + child);
            relationalEntityManagerUtil.linkBiDirChild(addedParent, child);
            // new parents may be detached, so merge them, must happen after linking!
//            entityManager.merge(addedParent);
        }
//        entityManager.merge(child); wont do no harm, maybe needed if child is detached?
        return addedParents;
    }

    public List<IdentifiableEntity> updateBiDirParentRelations(IdentifiableEntity oldParent, IdentifiableEntity parent) throws BadEntityException, EntityNotFoundException {

        Collection<IdentifiableEntity> oldChildren = relationalEntityManagerUtil.findAllBiDirChildren(oldParent);
        Collection<IdentifiableEntity> newChildren = relationalEntityManagerUtil.findAllBiDirChildren(parent);

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
            }
        }

        adjustUpdatedEntities(addedChildren, removedChildren);

        //unlink removed Children from parent
        for (IdentifiableEntity removedChild : removedChildren) {
            log.debug("unlinking child: " + removedChild + " from parent: " + parent);
//            relationalEntityManagerUtil.unlinkBiDirParent(removedChild, oldParent);
            relationalEntityManagerUtil.unlinkBiDirParent(removedChild, parent); // somehow does not make a difference but makes more sense like that imo
        }

        //link added Children to parent
        for (IdentifiableEntity addedChild : addedChildren) {
            log.debug("linking child: " + addedChild + " to parent: " + parent);
            relationalEntityManagerUtil.linkBiDirParent(addedChild, parent);
            // new children may be detached, so merge them , must happen after linking!
//            entityManager.merge(addedChild);
        }
        return addedChildren;
//        entityManager.merge(parent); wont do no harm, maybe needed if newChild is detached?
    }

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

    private void replaceParentsChildRefAndMerge(IdentifiableEntity biDirChild) {
        //set backreferences

        for (IdentifiableEntity parent : relationalEntityManagerUtil.findAllBiDirParents(biDirChild)) {
            relationalEntityManagerUtil.linkBiDirChild(parent,biDirChild);
            entityManager.merge(parent);
        }
    }

    private void replaceChildrensParentRefAndMerge(IdentifiableEntity biDirParent) {
        //set backreferences
        for (IdentifiableEntity child : relationalEntityManagerUtil.findAllBiDirChildren(biDirParent)) {
            relationalEntityManagerUtil.linkBiDirParent(child,biDirParent);
            entityManager.merge(child);
        }
    }

    private void updateAddedChildren(IdentifiableEntity parent, List<IdentifiableEntity> addedChildren) {
        //set backreferences
        for (IdentifiableEntity child : addedChildren) {
            relationalEntityManagerUtil.linkBiDirParent(child,parent);
            entityManager.merge(child);
        }
    }

    private void updateAddedParents(IdentifiableEntity child, List<IdentifiableEntity> addedParents) {
        //set backreferences
        for (IdentifiableEntity parent : addedParents) {
            relationalEntityManagerUtil.linkBiDirChild(parent,child);
            entityManager.merge(parent);
        }
    }


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
