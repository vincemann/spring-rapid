package com.github.vincemann.springrapid.entityrelationship.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.*;

@Aspect
@Slf4j
/**
 * Advice that keeps BiDirRelationships intact for {@link com.github.vincemann.springrapid.core.service.CrudService#update(IdentifiableEntity, Boolean)} - operations.
 */
public class BiDirEntityUpdateAdvice {

    private CrudServiceLocator serviceLocator;

    @Autowired
    public BiDirEntityUpdateAdvice(CrudServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirChild)")
    public void preUpdateBiDirChild(BiDirChild biDirChild) throws EntityNotFoundException, BadEntityException, BadEntityException {
        try {
            if(((IdentifiableEntity) biDirChild).getId()!=null) {
                log.debug("detected update operation for BiDirChild: " + biDirChild +", running preUpdateAdvice logic");
                updateBiDirChildRelations(biDirChild);
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirParent)")
    public void preUpdateBiDirParent(BiDirParent biDirParent) throws EntityNotFoundException, BadEntityException {
        try {
            if(((IdentifiableEntity) biDirParent).getId()!=null) {
                log.debug("detected update operation for BiDirParent: " + biDirParent + ", running preUpdateAdvice logic");
                updateBiDirParentRelations(biDirParent);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }



    @SuppressWarnings("Duplicates")
    private void updateBiDirChildRelations(BiDirChild newBiDirChild) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        //find already persisted biDirChild (preUpdateState of child)
        Class entityClazz = newBiDirChild.getClass();
        CrudService service = serviceLocator.find((Class<IdentifiableEntity>) entityClazz);
        Optional<BiDirChild> oldBiDirChildOptional = service.findById(((IdentifiableEntity<Serializable>) newBiDirChild).getId());
        VerifyEntity.isPresent(oldBiDirChildOptional,((IdentifiableEntity<Serializable>) newBiDirChild).getId(), entityClazz);
        BiDirChild oldBiDirChild = oldBiDirChildOptional.get();
        Collection<BiDirParent> oldParents = oldBiDirChild.findBiDirParents();
        Collection<BiDirParent> newParents = newBiDirChild.findBiDirParents();
        //find removed parents
        List<BiDirParent> removedParents = new ArrayList<>();
        for (BiDirParent oldParent : oldParents) {
            if(!newParents.contains(oldParent)){
                removedParents.add(oldParent);
            }
        }
        //find added parents
        List<BiDirParent> addedParents = new ArrayList<>();
        for (BiDirParent newParent : newParents) {
            if(!oldParents.contains(newParent)){
                addedParents.add(newParent);
            }
        }

        //dismiss removed Parents Children
        for (BiDirParent removedParent : removedParents) {
            removedParent.dismissBiDirChild(newBiDirChild);
        }

        //add added Parent to child
        for (BiDirParent addedParent : addedParents) {
            addedParent.addBiDirChild(newBiDirChild);
        }
    }


    @SuppressWarnings("Duplicates")
    private void updateBiDirParentRelations(BiDirParent newBiDirParent) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        Class entityClass = newBiDirParent.getClass();
        CrudService service = serviceLocator.find((Class<IdentifiableEntity>)entityClass);
        Optional<BiDirParent> oldBiDirParentOptional = service.findById(((IdentifiableEntity<Serializable>) newBiDirParent).getId());
        VerifyEntity.isPresent(oldBiDirParentOptional,((IdentifiableEntity<Serializable>) newBiDirParent).getId(),newBiDirParent.getClass());
        BiDirParent oldBiDirParent = oldBiDirParentOptional.get();

        Set<BiDirChild> oldChildren = oldBiDirParent.findBiDirSingleChildren();
        Set<BiDirChild> newChildren = newBiDirParent.findBiDirSingleChildren();

        Set<Collection<BiDirChild>> oldChildrenCollections = oldBiDirParent.findAllBiDirChildCollections().keySet();
        Set<Collection<BiDirChild>> newChildrenCollections = newBiDirParent.findAllBiDirChildCollections().keySet();

        //find removed Children
        List<BiDirChild> removedChildren = new ArrayList<>();
        for (BiDirChild oldChild : oldChildren) {
            if(!newChildren.contains(oldChild)){
                removedChildren.add(oldChild);
            }
        }
        for (Collection<? extends BiDirChild> oldChildrenCollection : oldChildrenCollections) {
            for (BiDirChild oldChild : oldChildrenCollection) {
                if(!newChildren.contains(oldChild)){
                    removedChildren.add(oldChild);
                }
            }
        }

        //find added Children
        List<BiDirChild> addedChildren = new ArrayList<>();
        for (BiDirChild newChild : newChildren) {
            if(!oldChildren.contains(newChild)){
                addedChildren.add(newChild);
            }
        }

        for (Collection<? extends BiDirChild> newChildrenCollection : newChildrenCollections) {
            for (BiDirChild newChild : newChildrenCollection) {
                if(!oldChildren.contains(newChild)){
                    addedChildren.add(newChild);
                }
            }
        }

        //dismiss removed Children from newParent
        for (BiDirChild removedChild : removedChildren) {
            log.debug("dismissing child: " + removedChild + " from parent: " +newBiDirParent);
            removedChild.dismissBiDirParent(oldBiDirParent);
        }

        //add added Children to newParent
        for (BiDirChild addedChild : addedChildren) {
            log.debug("adding child: " + addedChild + " to parent: " +newBiDirParent);
            addedChild.addBiDirParent(newBiDirParent);
        }
    }
}
