package com.github.vincemann.springrapid.entityrelationship.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.*;

@Aspect
@Slf4j
/**
 * Advice that keeps BiDirRelationships intact for Repo save operations (also update)
 */
public class BiDirEntitySaveAdvice {


    private CrudServiceLocator serviceLocator;


    @Autowired
    public BiDirEntitySaveAdvice(CrudServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }



    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirParent)")
    public void prePersistBiDirParent(BiDirParent biDirParent) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        if(((IdentifiableEntity) biDirParent).getId()==null) {
            log.debug("pre persist biDirParent hook reached for: " + biDirParent);
            setChildrensParentRef(biDirParent);
        }else {
            updateBiDirParentRelations(biDirParent);
        }
    }

    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirChild)")
    public void prePersistBiDiChild(BiDirChild biDirChild) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        if(((IdentifiableEntity) biDirChild).getId()==null) {
            log.debug("pre persist biDirChild hook reached for: " + biDirChild);
            setParentsChildRef(biDirChild);
        }
        else {
//            entityManager.merge(biDirChild);
            updateBiDirChildRelations(biDirChild);
            // need to replace child here for update parent situation (replace detached child with session attached child (this))
            replaceParentsChildRef(biDirChild);

        }
    }

    private void setChildrensParentRef(BiDirParent biDirParent){
        Set<? extends BiDirChild> children = biDirParent.findBiDirSingleChildren();
        for (BiDirChild child : children) {
            child.linkBiDirParent(biDirParent);
        }
        Set<Collection<BiDirChild>> childCollections = biDirParent.findBiDirChildCollections().keySet();
        for (Collection<BiDirChild> childCollection : childCollections) {
            for (BiDirChild biDirChild : childCollection) {
                biDirChild.linkBiDirParent(biDirParent);
            }
        }
    }

    private void replaceParentsChildRef(BiDirChild biDirChild) {
        //set backreferences
        for (BiDirParent parent : biDirChild.findBiDirParents()) {
            // check if BiDirChild is present before
            parent.unlinkBiDirChild(biDirChild);
            parent.linkBiDirChild(biDirChild);
        }
    }

    private void setParentsChildRef(BiDirChild biDirChild) {
        //set backreferences
        for (BiDirParent parent : biDirChild.findBiDirParents()) {
            parent.linkBiDirChild(biDirChild);
        }
    }

    @SuppressWarnings("Duplicates")
    private void updateBiDirChildRelations(BiDirChild newChild) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        BiDirChild oldChild = findOldEntity(newChild);

        Collection<BiDirParent> oldParents = oldChild.findBiDirParents();
        Collection<BiDirParent> newParents = newChild.findBiDirParents();
        //find parents to unlink
        List<BiDirParent> removedParents = new ArrayList<>();
        for (BiDirParent oldParent : oldParents) {
            if (!newParents.contains(oldParent)) {
                removedParents.add(oldParent);
            }
        }
        //find added parents
        List<BiDirParent> addedParents = new ArrayList<>();
        for (BiDirParent newParent : newParents) {
            if (!oldParents.contains(newParent)) {
                addedParents.add(newParent);
            }
        }

        adjustUpdatedEntities(addedParents,removedParents);

        //unlink Child from certain Parents
        for (BiDirParent removedParent : removedParents) {
            removedParent.unlinkBiDirChild(oldChild);
        }

        //add added Parent to child
        for (BiDirParent addedParent : addedParents) {
            addedParent.linkBiDirChild(newChild);
        }
    }


    @SuppressWarnings("Duplicates")
    private void updateBiDirParentRelations(BiDirParent newParent) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        BiDirParent oldParent = findOldEntity(newParent);

        Set<BiDirChild> oldSingleChildren = oldParent.findBiDirSingleChildren();
        Set<BiDirChild> newSingleChildren = newParent.findBiDirSingleChildren();

        Set<Collection<BiDirChild>> oldChildCollections = oldParent.findBiDirChildCollections().keySet();
        Set<Collection<BiDirChild>> newChildCollections = newParent.findBiDirChildCollections().keySet();

        //find Children to unlink
        List<BiDirChild> removedChildren = new ArrayList<>();
        for (BiDirChild oldChild : oldSingleChildren) {
            if (!newSingleChildren.contains(oldChild)) {
                removedChildren.add(oldChild);
            }
        }
        for (Collection<? extends BiDirChild> oldChildrenCollection : oldChildCollections) {
            for (BiDirChild oldChild : oldChildrenCollection) {
                if (!newSingleChildren.contains(oldChild)) {
                    removedChildren.add(oldChild);
                }
            }
        }

        //find added Children
        List<BiDirChild> addedChildren = new ArrayList<>();
        for (BiDirChild newChild : newSingleChildren) {
            if (!oldSingleChildren.contains(newChild)) {
                addedChildren.add(newChild);
            }
        }
        for (Collection<? extends BiDirChild> newChildrenCollection : newChildCollections) {
            for (BiDirChild newChild : newChildrenCollection) {
                if (!oldSingleChildren.contains(newChild)) {
                    addedChildren.add(newChild);
                }
            }
        }

        adjustUpdatedEntities(addedChildren,removedChildren);

        //unlink removed Children from newParent
        for (BiDirChild removedChild : removedChildren) {
            log.debug("unlinking child: " + removedChild + " from parent: " + newParent);
            removedChild.unlinkBiDirParent(oldParent);
        }

        //link added Children to newParent
        for (BiDirChild addedChild : addedChildren) {
            log.debug("linking child: " + addedChild + " to parent: " + newParent);
            addedChild.linkBiDirParent(newParent);
        }
    }

    private <E> void adjustUpdatedEntities(List<E> added, List<E> removed){
        removed.removeAll(added);
        added.removeAll(removed);
    }

    private <E> E findOldEntity(E entity) throws EntityNotFoundException, BadEntityException {
        Class entityClass = entity.getClass();
        CrudService service = serviceLocator.find((Class<IdentifiableEntity>) entityClass);
        Optional<BiDirParent> oldEntityOptional = service.findById(((IdentifiableEntity<Serializable>) entity).getId());
        VerifyEntity.isPresent(oldEntityOptional, ((IdentifiableEntity<Serializable>) entity).getId(), entity.getClass());
        return (E) oldEntityOptional.get();
    }
}
