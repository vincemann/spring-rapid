package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Transactional
public class RapidRelationalEntityManager implements RelationalEntityManager {

    private RelationalEntityManagerUtil relationalEntityManagerUtil;

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
//            if (entity.getId() == null) {
            //create
            relationalEntityManagerUtil.linkChildrensParent(entity);
//            } else {
//                // update
//                log.debug("pre update biDirParent hook reached for: " + entity);
//                updateBiDirParentRelations(entity);
//                // need to replace child here for partial update entity situation (replace detached child with session attached child (this))
//                replaceChildrensParentRef(entity);
//                // needs to be done to prevent detached error when adding entity to child via full update or save
//                mergeParentsChildren(entity);
//            }
        }

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            log.debug("applying pre persist BiDirChild logic for: " + entity);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirParentEntity.class);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirParentCollection.class);
//            if ( entity.getId() == null) {
//                //create
            relationalEntityManagerUtil.linkParentsChild(entity);
//            } else {
//                // update
//                log.debug("pre update biDirChild hook reached for: " + entity);
//                updateBiDirChildRelations(entity);
//                // need to replace child here for partial update parent situation (replace detached child with session attached child (this))
//                replaceParentsChildRef(entity);
//                // needs to be done to prevent detached error when adding parent to child via full update or save
//                mergeChildrensParents(entity);
//            }
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
//            BiDirChild biDirChild = (BiDirChild) entity;
//            for (BiDirParent parent : biDirChild.findSingleBiDirParents()) {
//                parent.unlinkBiDirChild(biDirChild);
//            }
//            biDirChild.unlinkBiDirParents();
            relationalEntityManagerUtil.unlinkParentsChild(entity);
        }
    }


    @Override
    public <E extends IdentifiableEntity> E partialUpdate(E oldEntity, E updateEntity, E partialUpdateEntity) throws EntityNotFoundException, BadEntityException {
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(updateEntity.getClass());
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            log.debug("applying pre partial-update BiDirParent logic for: " + updateEntity.getClass());
            updateBiDirParentRelations(oldEntity, updateEntity);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            log.debug("applying pre partial-update BiDirChild logic for: " + updateEntity.getClass());
            updateBiDirChildRelations(oldEntity, updateEntity);
        }
        return updateEntity;
    }

    @Override
    public <E extends IdentifiableEntity> E update(E oldEntity, E updateEntity) throws EntityNotFoundException, BadEntityException {
//        E oldEntity = findOldEntity(updateEntity);
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(updateEntity.getClass());

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            log.debug("applying pre full-update BiDirParent logic for: " + updateEntity.getClass());
            updateBiDirParentRelations(oldEntity, updateEntity);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            log.debug("applying pre full-update BiDirChild logic for: " + updateEntity.getClass());
            updateBiDirChildRelations(oldEntity, updateEntity);
        }

        return updateEntity;
    }

    public void updateBiDirChildRelations(IdentifiableEntity oldChild, IdentifiableEntity newChild) throws BadEntityException, EntityNotFoundException {

        Collection<IdentifiableEntity> oldParents = relationalEntityManagerUtil.findAllBiDirParents(oldChild);
        Collection<IdentifiableEntity> newParents = relationalEntityManagerUtil.findAllBiDirParents(newChild);

//        Collection<Collection<IdentifiableEntity>> oldParentCollections = relationalEntityManagerUtil.findBiDirParentCollections(oldChild).values();
//        Collection<Collection<IdentifiableEntity>> newParentCollections = relationalEntityManagerUtil.findBiDirParentCollections(newChild).values();

        //find parents to unlink
        List<IdentifiableEntity> removedParents = new ArrayList<>();
        for (IdentifiableEntity oldParent : oldParents) {
            if (!newParents.contains(oldParent)) {
                removedParents.add(oldParent);
            }
        }

//        for (Collection<? extends IdentifiableEntity> oldParentCollection : oldParentCollections) {
//            for (IdentifiableEntity oldParent : oldParentCollection) {
//                if (!newParents.contains(oldParent)) {
//                    removedParents.add(oldParent);
//                }
//            }
//        }


        //find added parents
        List<IdentifiableEntity> addedParents = new ArrayList<>();
        for (IdentifiableEntity newParent : newParents) {
            if (!oldParents.contains(newParent)) {
                addedParents.add(newParent);
            }
        }

//        for (Collection<? extends IdentifiableEntity> newParentCollection : newParentCollections) {
//            for (IdentifiableEntity newParent : newParentCollection) {
//                if (!oldParents.contains(newParent)) {
//                    addedParents.add(newParent);
//                }
//            }
//        }

        adjustUpdatedEntities(addedParents, removedParents);

        // unlink Child from certain Parents
        for (IdentifiableEntity removedParent : removedParents) {
            log.debug("update: unlinking parent: " + removedParent + " from child: " + newChild);
            relationalEntityManagerUtil.unlinkBiDirChild(removedParent, oldChild);
        }

        // link added Parent to child
        for (IdentifiableEntity addedParent : addedParents) {
            log.debug("update: linking parent: " + addedParent + " to child: " + newChild);
            relationalEntityManagerUtil.linkBiDirChild(addedParent, newChild);
        }
    }

    public void updateBiDirParentRelations(IdentifiableEntity oldParent, IdentifiableEntity newParent) throws BadEntityException, EntityNotFoundException {

        Collection<IdentifiableEntity> oldChildren = relationalEntityManagerUtil.findAllBiDirChildren(oldParent);
        Collection<IdentifiableEntity> newChildren = relationalEntityManagerUtil.findAllBiDirChildren(newParent);

//        Collection<Collection<IdentifiableEntity>> oldChildCollections = relationalEntityManagerUtil.findBiDirChildCollections(oldParent).values();
//        Collection<Collection<IdentifiableEntity>> newChildCollections = relationalEntityManagerUtil.findBiDirChildCollections(newParent).values();

        //find Children to unlink
        List<IdentifiableEntity> removedChildren = new ArrayList<>();
        for (IdentifiableEntity oldChild : oldChildren) {
            if (!newChildren.contains(oldChild)) {
                removedChildren.add(oldChild);
            }
        }
//        for (Collection<? extends IdentifiableEntity> oldChildrenCollection : oldChildCollections) {
//            for (IdentifiableEntity oldChild : oldChildrenCollection) {
//                if (!newChildren.contains(oldChild)) {
//                    removedChildren.add(oldChild);
//                }
//            }
//        }

        //find added Children
        List<IdentifiableEntity> addedChildren = new ArrayList<>();
        for (IdentifiableEntity newChild : newChildren) {
            if (!oldChildren.contains(newChild)) {
                addedChildren.add(newChild);
            }
        }


//        for (Collection<? extends IdentifiableEntity> newChildrenCollection : newChildCollections) {
//            // add util here to lazy load collection !
//
//            for (IdentifiableEntity newChild : newChildrenCollection) {
//                if (!oldChildren.contains(newChild)) {
//                    addedChildren.add(newChild);
//                }
//            }
//        }

        adjustUpdatedEntities(addedChildren, removedChildren);

        //unlink removed Children from newParent
        for (IdentifiableEntity removedChild : removedChildren) {
            log.debug("unlinking child: " + removedChild + " from parent: " + newParent);
            relationalEntityManagerUtil.unlinkBiDirParent(removedChild, oldParent);
        }

        //link added Children to newParent
        for (IdentifiableEntity addedChild : addedChildren) {
            log.debug("linking child: " + addedChild + " to parent: " + newParent);
            relationalEntityManagerUtil.linkBiDirParent(addedChild, newParent);
        }
    }

    protected <E> void adjustUpdatedEntities(List<E> added, List<E> removed) {
        removed.removeAll(added);
        added.removeAll(removed);
    }


//    private void mergeChildrensParents(IdentifiableEntity biDirChild) {
//        //set backreferences
//        Collection<Collection<IdentifiableEntity>> parentCollections = relationalEntityManagerUtil.findBiDirParentCollections(biDirChild).values();
//        for (Collection<IdentifiableEntity> parentCollection : parentCollections) {
//            for (IdentifiableEntity biDirParent : parentCollection) {
//                getEntityManager().merge(biDirParent);
//            }
//        }
//
//        for (IdentifiableEntity parent : relationalEntityManagerUtil.findSingleBiDirParents(biDirChild)) {
//            getEntityManager().merge(parent);
//        }
//    }

//    private void mergeParentsChildren(IdentifiableEntity biDirParent) {
//        Set<? extends IdentifiableEntity> children = relationalEntityManagerUtil.findSingleBiDirChildren(biDirParent);
//        for (IdentifiableEntity child : children) {
//            getEntityManager().merge(child);
//        }
//        Collection<Collection<IdentifiableEntity>> childCollections = relationalEntityManagerUtil.findBiDirChildCollections(biDirParent).values();
//        for (Collection<IdentifiableEntity> childCollection : childCollections) {
//            for (IdentifiableEntity biDirChild : childCollection) {
//                getEntityManager().merge(biDirChild);
//            }
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

//    private void replaceParentsChildRef(IdentifiableEntity biDirChild) {
//        //set backreferences
//
//        Collection<Collection<IdentifiableEntity>> parentCollections = relationalEntityManagerUtil.findBiDirParentCollections(biDirChild).values();
//        for (Collection<IdentifiableEntity> parentCollection : parentCollections) {
//            for (IdentifiableEntity biDirParent : parentCollection) {
//                relationalEntityManagerUtil.unlinkBiDirChild(biDirParent,biDirChild);
//                relationalEntityManagerUtil.linkBiDirChild(biDirParent,biDirChild);
//            }
//        }
//
//        for (IdentifiableEntity parent : relationalEntityManagerUtil.findSingleBiDirParents(biDirChild)) {
//            relationalEntityManagerUtil.unlinkBiDirChild(parent,biDirChild);
//            relationalEntityManagerUtil.linkBiDirChild(parent,biDirChild);
//        }
//    }

//    private void replaceChildrensParentRef(IdentifiableEntity biDirParent) {
//        //set backreferences
//
//        Collection<Collection<IdentifiableEntity>> childCollections = relationalEntityManagerUtil.findBiDirChildCollections(biDirParent).values();
//        for (Collection<IdentifiableEntity> childCollection : childCollections) {
//            for (IdentifiableEntity biDirChild : childCollection) {
//                relationalEntityManagerUtil.unlinkBiDirParent(biDirChild,biDirParent);
//                relationalEntityManagerUtil.linkBiDirParent(biDirChild,biDirParent);
//            }
//        }
//
//        for (IdentifiableEntity child : relationalEntityManagerUtil.findSingleBiDirChildren(biDirParent)) {
//            relationalEntityManagerUtil.unlinkBiDirParent(child,biDirParent);
//            relationalEntityManagerUtil.linkBiDirParent(child,biDirParent);
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
