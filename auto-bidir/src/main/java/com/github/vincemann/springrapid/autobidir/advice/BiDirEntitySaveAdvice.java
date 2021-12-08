package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Set;

@Aspect
@Slf4j

/**
 * Advice that keeps BiDirRelationships intact for Repo save operations (also update)
 */
public class BiDirEntitySaveAdvice extends BiDirEntityAdvice {


    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    public BiDirEntitySaveAdvice(CrudServiceLocator crudServiceLocator, RelationalEntityManager relationalEntityManager) {
        super(crudServiceLocator, relationalEntityManager);
    }

    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(entity)")
    public void prePersistBiDirEntity(IdentifiableEntity entity) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManager.inferTypes(entity.getClass());
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)){
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


    private void mergeChildrensParents(IdentifiableEntity biDirChild) {
        //set backreferences
        Collection<Collection<IdentifiableEntity>> parentCollections = relationalEntityManager.findBiDirParentCollections(biDirChild).values();
        for (Collection<IdentifiableEntity> parentCollection : parentCollections) {
            for (IdentifiableEntity biDirParent : parentCollection) {
                entityManager.merge(biDirParent);
            }
        }

        for (IdentifiableEntity parent : relationalEntityManager.findSingleBiDirParents(biDirChild)) {
            entityManager.merge(parent);
        }
    }

    private void mergeParentsChildren(IdentifiableEntity biDirParent) {
        Set<? extends IdentifiableEntity> children = relationalEntityManager.findSingleBiDirChildren(biDirParent);
        for (IdentifiableEntity child : children) {
            entityManager.merge(child);
        }
        Collection<Collection<IdentifiableEntity>> childCollections = relationalEntityManager.findBiDirChildCollections(biDirParent).values();
        for (Collection<IdentifiableEntity> childCollection : childCollections) {
            for (IdentifiableEntity biDirChild : childCollection) {
                entityManager.merge(biDirChild);
            }
        }
    }

    private void setChildrensParentRef(IdentifiableEntity biDirParent) {
        Set<? extends IdentifiableEntity> children = relationalEntityManager.findSingleBiDirChildren(biDirParent);
        for (IdentifiableEntity child : children) {
            relationalEntityManager.linkBiDirParent(child, biDirParent);
        }
        Collection<Collection<IdentifiableEntity>> childCollections = relationalEntityManager.findBiDirChildCollections(biDirParent).values();
        for (Collection<IdentifiableEntity> childCollection : childCollections) {
            for (IdentifiableEntity child : childCollection) {
                relationalEntityManager.linkBiDirParent(child, biDirParent);
            }
        }
    }

    private void replaceParentsChildRef(IdentifiableEntity biDirChild) {
        //set backreferences

        Collection<Collection<IdentifiableEntity>> parentCollections = relationalEntityManager.findBiDirParentCollections(biDirChild).values();
        for (Collection<IdentifiableEntity> parentCollection : parentCollections) {
            for (IdentifiableEntity biDirParent : parentCollection) {
                relationalEntityManager.unlinkBiDirChild(biDirParent,biDirChild);
                relationalEntityManager.linkBiDirChild(biDirParent,biDirChild);
            }
        }

        for (IdentifiableEntity parent : relationalEntityManager.findSingleBiDirParents(biDirChild)) {
            relationalEntityManager.unlinkBiDirChild(parent,biDirChild);
            relationalEntityManager.linkBiDirChild(parent,biDirChild);
        }
    }

    private void replaceChildrensParentRef(IdentifiableEntity biDirParent) {
        //set backreferences

        Collection<Collection<IdentifiableEntity>> childCollections = relationalEntityManager.findBiDirChildCollections(biDirParent).values();
        for (Collection<IdentifiableEntity> childCollection : childCollections) {
            for (IdentifiableEntity biDirChild : childCollection) {
                relationalEntityManager.unlinkBiDirParent(biDirChild,biDirParent);
                relationalEntityManager.linkBiDirParent(biDirChild,biDirParent);
            }
        }

        for (IdentifiableEntity child : relationalEntityManager.findSingleBiDirChildren(biDirParent)) {
            relationalEntityManager.unlinkBiDirParent(child,biDirParent);
            relationalEntityManager.linkBiDirParent(child,biDirParent);
        }
    }

    private void setParentsChildRef(IdentifiableEntity biDirChild) {
        //set backreferences

        Collection<Collection<IdentifiableEntity>> parentCollections = relationalEntityManager.findBiDirParentCollections(biDirChild).values();
        for (Collection<IdentifiableEntity> parentCollection : parentCollections) {
            for (IdentifiableEntity biDirParent : parentCollection) {
                relationalEntityManager.linkBiDirChild(biDirParent,biDirChild);
            }
        }

        for (IdentifiableEntity parent : relationalEntityManager.findSingleBiDirParents(biDirChild)) {
            relationalEntityManager.linkBiDirChild(parent,biDirChild);
        }

    }

}
