package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.util.BiDirJpaUtils;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.*;

@Getter
@Slf4j
public abstract class BiDirEntityAdvice {
    protected RelationalEntityManager relationalEntityManager;
    private CrudServiceLocator crudServiceLocator;
    @PersistenceContext
    private EntityManager entityManager;

    public BiDirEntityAdvice(CrudServiceLocator crudServiceLocator, RelationalEntityManager relationalEntityManager) {
        this.crudServiceLocator = crudServiceLocator;
        this.relationalEntityManager = relationalEntityManager;
    }

    public void updateBiDirParentRelations(IdentifiableEntity newParent) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        IdentifiableEntity oldParent = findOldEntity(newParent);
        newParent = BiDirJpaUtils.initializeSubEntities(newParent, BiDirChildCollection.class);

        Set<IdentifiableEntity> oldSingleChildren = relationalEntityManager.findSingleBiDirChildren(oldParent);
        Set<IdentifiableEntity> newSingleChildren = relationalEntityManager.findSingleBiDirChildren(newParent);

        Collection<Collection<IdentifiableEntity>> oldChildCollections = relationalEntityManager.findBiDirChildCollections(oldParent).values();
        Collection<Collection<IdentifiableEntity>> newChildCollections = relationalEntityManager.findBiDirChildCollections(newParent).values();

        //find Children to unlink
        List<IdentifiableEntity> removedChildren = new ArrayList<>();
        for (IdentifiableEntity oldChild : oldSingleChildren) {
            if (!newSingleChildren.contains(oldChild)) {
                removedChildren.add(oldChild);
            }
        }
        for (Collection<? extends IdentifiableEntity> oldChildrenCollection : oldChildCollections) {
            for (IdentifiableEntity oldChild : oldChildrenCollection) {
                if (!newSingleChildren.contains(oldChild)) {
                    removedChildren.add(oldChild);
                }
            }
        }

        //find added Children
        List<IdentifiableEntity> addedChildren = new ArrayList<>();
        for (IdentifiableEntity newChild : newSingleChildren) {
            if (!oldSingleChildren.contains(newChild)) {
                addedChildren.add(newChild);
            }
        }


        for (Collection<? extends IdentifiableEntity> newChildrenCollection : newChildCollections) {
            // add util here to lazy load collection !

            for (IdentifiableEntity newChild : newChildrenCollection) {
                if (!oldSingleChildren.contains(newChild)) {
                    addedChildren.add(newChild);
                }
            }
        }

        adjustUpdatedEntities(addedChildren, removedChildren);

        //unlink removed Children from newParent
        for (IdentifiableEntity removedChild : removedChildren) {
            log.debug("unlinking child: " + removedChild + " from parent: " + newParent);
            relationalEntityManager.unlinkBiDirParent(removedChild, oldParent);
        }

        //link added Children to newParent
        for (IdentifiableEntity addedChild : addedChildren) {
            log.debug("linking child: " + addedChild + " to parent: " + newParent);
            relationalEntityManager.linkBiDirParent(addedChild, newParent);
        }
    }

    protected void updateBiDirChildRelations(IdentifiableEntity newChild) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
        IdentifiableEntity oldChild = findOldEntity(newChild);

        Collection<IdentifiableEntity> oldSingleParents = relationalEntityManager.findSingleBiDirParents(oldChild);
        Collection<IdentifiableEntity> newSinlgeParents = relationalEntityManager.findSingleBiDirParents(newChild);

        Collection<Collection<IdentifiableEntity>> oldParentCollections = relationalEntityManager.findBiDirParentCollections(oldChild).values();
        Collection<Collection<IdentifiableEntity>> newParentCollections = relationalEntityManager.findBiDirParentCollections(newChild).values();

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
            relationalEntityManager.unlinkBiDirChild(removedParent, oldChild);
        }

        //add added Parent to child
        for (IdentifiableEntity addedParent : addedParents) {
            log.debug("linking parent: " + addedParent + " to child: " + newChild);
            relationalEntityManager.linkBiDirChild(addedParent, newChild);
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
}
