package io.github.vincemann.springrapid.core.advice;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.model.biDir.child.BiDirChild;
import io.github.vincemann.springrapid.core.model.biDir.parent.BiDirParent;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.exception.NoIdException;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Aspect
@Component
@Slf4j
public class BiDirEntityUpdateAdvice {

    private CrudServiceLocator serviceLocator;

    @Autowired
    public BiDirEntityUpdateAdvice(CrudServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    @Before("io.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "io.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirChild)")
    public void preUpdateBiDirChild(BiDirChild biDirChild) throws EntityNotFoundException, NoIdException, BadEntityException {
        try {
            if(((IdentifiableEntity) biDirChild).getId()!=null) {
                log.debug("detected update operation for BiDirChild: " + biDirChild +", running preUpdateAdvice logic");
                updateBiDirChildRelations(biDirChild);
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    @Before("io.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "io.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirParent)")
    public void preUpdateBiDirParent(BiDirParent biDirParent) throws EntityNotFoundException, NoIdException {
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
    private void updateBiDirChildRelations(BiDirChild newBiDirChild) throws NoIdException, EntityNotFoundException, IllegalAccessException {
        //find already persisted biDirChild (preUpdateState of child)
        CrudService service = serviceLocator.find((Class<? extends IdentifiableEntity>) newBiDirChild.getClass());
        Optional<BiDirChild> oldBiDirChildOptional = service.findById(((IdentifiableEntity<Serializable>) newBiDirChild).getId());
        if(!oldBiDirChildOptional.isPresent()){
            throw new EntityNotFoundException(((IdentifiableEntity<Serializable>) newBiDirChild).getId(),newBiDirChild.getClass());
        }
        BiDirChild oldBiDirChild = oldBiDirChildOptional.get();
        Collection<BiDirParent> oldParents = oldBiDirChild.findParents();
        Collection<BiDirParent> newParents = newBiDirChild.findParents();
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
            removedParent.dismissChild(newBiDirChild);
        }

        //add added Parent to child
        for (BiDirParent addedParent : addedParents) {
            addedParent.addChild(newBiDirChild);
        }
    }


    @SuppressWarnings("Duplicates")
    private void updateBiDirParentRelations(BiDirParent newBiDirParent) throws NoIdException, EntityNotFoundException, IllegalAccessException {
        CrudService service = serviceLocator.find((Class<? extends IdentifiableEntity>) newBiDirParent.getClass());
        Optional<BiDirParent> oldBiDirParentOptional = service.findById(((IdentifiableEntity<Serializable>) newBiDirParent).getId());
        if(!oldBiDirParentOptional.isPresent()){
            throw new EntityNotFoundException(((IdentifiableEntity<Serializable>) newBiDirParent).getId(),newBiDirParent.getClass());
        }
        BiDirParent oldBiDirParent = oldBiDirParentOptional.get();

        Set<? extends BiDirChild> oldChildren = oldBiDirParent.getChildren();
        Set<? extends BiDirChild> newChildren = newBiDirParent.getChildren();

        Set<Collection<? extends BiDirChild>> oldChildrenCollections = oldBiDirParent.getChildrenCollections().keySet();
        Set<Collection<? extends BiDirChild>> newChildrenCollections = newBiDirParent.getChildrenCollections().keySet();

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
            removedChild.dismissParent(oldBiDirParent);
        }

        //add added Children to newParent
        for (BiDirChild addedChild : addedChildren) {
            log.debug("adding child: " + addedChild + " to parent: " +newBiDirParent);
            addedChild.setParentRef(newBiDirParent);
        }
    }
}
