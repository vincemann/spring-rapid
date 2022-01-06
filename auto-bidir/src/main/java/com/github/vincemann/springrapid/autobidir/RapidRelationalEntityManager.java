package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildEntity;
import com.github.vincemann.springrapid.autobidir.util.BiDirJpaUtils;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.*;

@Slf4j
public class RapidRelationalEntityManager implements RelationalEntityManager {

    private RelationalEntityManagerUtil relationalEntityManagerUtil;

    @Override
    public <E extends IdentifiableEntity> E save(E entity) {
        if (entity.getId() != null){
            throw new IllegalArgumentException("Save needs null id");
        }
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManagerUtil.inferTypes(entity.getClass());
        // subscribedUsers is 1 instead of 0
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)){
            // also filter for class obj stored in annotation, so if I update only one BiDirChildCollection, only init this one
            // with the right class
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirChildCollection.class);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirChildEntity.class);
            if (entity.getId() == null) {
                //create
                log.debug("pre persist biDirParent hook reached for: " + entity);
                setChildrensParentRef(entity);
            } else {
                // update
                log.debug("pre update biDirParent hook reached for: " + entity);
                updateBiDirParentRelations(entity);
                // need to replace child here for partial update entity situation (replace detached child with session attached child (this))
                replaceChildrensParentRef(entity);
                // needs to be done to prevent detached error when adding entity to child via full update or save
                mergeParentsChildren(entity);
            }
        }

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)){
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirParentEntity.class);
//            entity = BiDirJpaUtils.initializeSubEntities(entity, BiDirParentCollection.class);
            if ( entity.getId() == null) {
                //create
                log.debug("pre persist biDirChild hook reached for: " + entity);
                setParentsChildRef(entity);
            } else {
                // update
                log.debug("pre update biDirChild hook reached for: " + entity);
                updateBiDirChildRelations(entity);
                // need to replace child here for partial update parent situation (replace detached child with session attached child (this))
                replaceParentsChildRef(entity);
                // needs to be done to prevent detached error when adding parent to child via full update or save
                mergeChildrensParents(entity);
            }
        }
    }

    public void updateBiDirChildRelations(IdentifiableEntity newChild) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        IdentifiableEntity oldChild = findOldEntity(newChild);

        Collection<IdentifiableEntity> oldSingleParents = relationalEntityManagerUtil.findSingleBiDirParents(oldChild);
        Collection<IdentifiableEntity> newSinlgeParents = relationalEntityManagerUtil.findSingleBiDirParents(newChild);

        Collection<Collection<IdentifiableEntity>> oldParentCollections = relationalEntityManagerUtil.findBiDirParentCollections(oldChild).values();
        Collection<Collection<IdentifiableEntity>> newParentCollections = relationalEntityManagerUtil.findBiDirParentCollections(newChild).values();

        //find parents to unlink
        List<IdentifiableEntity> removedParents = new ArrayList<>();
        for (IdentifiableEntity oldParent : oldSingleParents) {
            if (!newSinlgeParents.contains(oldParent)) {
                removedParents.add(oldParent);
            }
        }

        for (Collection<? extends IdentifiableEntity> oldParentCollection : oldParentCollections) {
            for (IdentifiableEntity oldParent : oldParentCollection) {
                if (!newSinlgeParents.contains(oldParent)) {
                    removedParents.add(oldParent);
                }
            }
        }


        //find added parents
        List<IdentifiableEntity> addedParents = new ArrayList<>();
        for (IdentifiableEntity newParent : newSinlgeParents) {
            if (!oldSingleParents.contains(newParent)) {
                addedParents.add(newParent);
            }
        }

        for (Collection<? extends IdentifiableEntity> newParentCollection : newParentCollections) {
            for (IdentifiableEntity newParent : newParentCollection) {
                if (!oldSingleParents.contains(newParent)) {
                    addedParents.add(newParent);
                }
            }
        }

        adjustUpdatedEntities(addedParents, removedParents);

        //unlink Child from certain Parents
        for (IdentifiableEntity removedParent : removedParents) {
            log.debug("unlinking parent: " + removedParent + " from child: " + newChild);
            relationalEntityManagerUtil.unlinkBiDirChild(removedParent, oldChild);
        }

        //add added Parent to child
        for (IdentifiableEntity addedParent : addedParents) {
            log.debug("linking parent: " + addedParent + " to child: " + newChild);
            relationalEntityManagerUtil.linkBiDirChild(addedParent, newChild);
        }
    }

    protected <E> void adjustUpdatedEntities(List<E> added, List<E> removed) {
        removed.removeAll(added);
        added.removeAll(removed);
    }

    protected <E> E findOldEntity(E entity) throws EntityNotFoundException, BadEntityException {
        Class entityClass = entity.getClass();
        CrudService service = crudServiceLocator.find((Class<IdentifiableEntity>) entityClass);
        Optional<IdentifiableEntity> oldEntityOptional = service.findById(((IdentifiableEntity<Serializable>) entity).getId());
        VerifyEntity.isPresent(oldEntityOptional, ((IdentifiableEntity<Serializable>) entity).getId(), entity.getClass());
        return (E) oldEntityOptional.get();
    }

    @Override
    public void remove(IdentifiableEntity entity) {

    }

    @Override
    public <E extends IdentifiableEntity> E update(E entity, Boolean full) {
        return null;
    }


    @Autowired
    public void setRelationalEntityManagerUtil(RelationalEntityManagerUtil relationalEntityManagerUtil) {
        this.relationalEntityManagerUtil = relationalEntityManagerUtil;
    }
}
