package com.github.vincemann.springrapid.entityrelationship.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;

@Getter
@Slf4j
public abstract class BiDirEntityAdvice {
    private CrudServiceLocator crudServiceLocator;

    public BiDirEntityAdvice(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }

    protected void updateBiDirParentRelations(BiDirParent newParent) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        BiDirParent oldParent = findOldEntity(newParent);

        Set<BiDirChild> oldSingleChildren = oldParent.findSingleBiDirChildren();
        Set<BiDirChild> newSingleChildren = newParent.findSingleBiDirChildren();

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

    protected void updateBiDirChildRelations(BiDirChild newChild) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        BiDirChild oldChild = findOldEntity(newChild);

        Collection<BiDirParent> oldSingleParents = oldChild.findSingleBiDirParents();
        Collection<BiDirParent> newSinlgeParents = newChild.findSingleBiDirParents();

        Set<Collection<BiDirParent>> oldParentCollections = oldChild.findBiDirParentCollections().keySet();
        Set<Collection<BiDirParent>> newParentCollections = newChild.findBiDirParentCollections().keySet();

        //find parents to unlink
        List<BiDirParent> removedParents = new ArrayList<>();
        for (BiDirParent oldParent : oldSingleParents) {
            if (!newSinlgeParents.contains(oldParent)) {
                removedParents.add(oldParent);
            }
        }

        for (Collection<? extends BiDirParent> oldParentCollection : oldParentCollections) {
            for (BiDirParent oldParent : oldParentCollection) {
                if (!newSinlgeParents.contains(oldParent)) {
                    removedParents.add(oldParent);
                }
            }
        }


        //find added parents
        List<BiDirParent> addedParents = new ArrayList<>();
        for (BiDirParent newParent : newSinlgeParents) {
            if (!oldSingleParents.contains(newParent)) {
                addedParents.add(newParent);
            }
        }

        for (Collection<? extends BiDirParent> newParentCollection : newParentCollections) {
            for (BiDirParent newParent : newParentCollection) {
                if (!oldSingleParents.contains(newParent)) {
                    addedParents.add(newParent);
                }
            }
        }

        adjustUpdatedEntities(addedParents,removedParents);

        //unlink Child from certain Parents
        for (BiDirParent removedParent : removedParents) {
            log.debug("unlinking parent: " + removedParent + " from child: " + newChild);
            removedParent.unlinkBiDirChild(oldChild);
        }

        //add added Parent to child
        for (BiDirParent addedParent : addedParents) {
            log.debug("linking parent: " + addedParent + " to child: " + newChild);
            addedParent.linkBiDirChild(newChild);
        }
    }

    protected  <E> void adjustUpdatedEntities(List<E> added, List<E> removed){
        removed.removeAll(added);
        added.removeAll(removed);
    }

    protected  <E> E findOldEntity(E entity) throws EntityNotFoundException, BadEntityException {
        Class entityClass = entity.getClass();
        CrudService service = crudServiceLocator.find((Class<IdentifiableEntity>) entityClass);
        Optional<BiDirParent> oldEntityOptional = service.findById(((IdentifiableEntity<Serializable>) entity).getId());
        VerifyEntity.isPresent(oldEntityOptional, ((IdentifiableEntity<Serializable>) entity).getId(), entity.getClass());
        return (E) oldEntityOptional.get();
    }
}
