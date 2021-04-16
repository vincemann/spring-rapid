package com.github.vincemann.springrapid.entityrelationship.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.*;

@Aspect
@Slf4j
@Transactional
/**
 * Advice that keeps BiDirRelationships intact for repo save operations that are updates (id is set)
 */
public class BiDirEntityUpdateAdvice {

    private CrudServiceLocator serviceLocator;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public BiDirEntityUpdateAdvice(CrudServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(biDirChild,full)")
    public void preUpdateBiDirChild(JoinPoint joinPoint, BiDirChild biDirChild, Boolean full) throws EntityNotFoundException, BadEntityException {
        try {
            if (!isRootService(joinPoint)) {
                log.debug("ignoring service update advice, bc root service not called yet");
                return;
            }

            if (((IdentifiableEntity) biDirChild).getId() != null && !full) {
                log.debug("detected service partial update operation for BiDirChild: " + biDirChild + ", running preUpdateAdvice logic");
                updateBiDirChildRelations(biDirChild);
            }// else ignore, bc it is save operation not update
//            else if (full){
//                entityManager.merge(biDirChild);
//            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(biDirParent,full)")
    public void preUpdateBiDirParent(JoinPoint joinPoint, BiDirParent biDirParent, Boolean full) throws EntityNotFoundException, BadEntityException {
        try {
            if (!isRootService(joinPoint)) {
                log.debug("ignoring service update advice, bc root service not called yet");
                return;
            }
            if (((IdentifiableEntity) biDirParent).getId() != null && !full) {
                log.debug("detected service partial update operation for BiDirParent: " + biDirParent + ", running preUpdateAdvice logic");
                updateBiDirParentRelations(biDirParent);
            } // else ignore, bc it is save operation not update
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


//    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
//            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
//            "args(biDirChild)")
//    public void preUpdateBiDirChild(JoinPoint joinPoint, BiDirChild biDirChild) throws EntityNotFoundException, BadEntityException {
//        try {
////            if (!isRootService(joinPoint)) {
////                log.debug("ignoring update advice, bc root service not called yet");
////                return;
////            }
//            if (((IdentifiableEntity) biDirChild).getId() != null) {
//                log.debug("detected update operation for BiDirChild: " + biDirChild + ", running preUpdateAdvice logic");
//                updateBiDirChildRelations(biDirChild);
//            }// else ignore, bc it is save operation not update
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
//            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
//            "args(biDirParent)")
//    public void preUpdateBiDirParent(JoinPoint joinPoint, BiDirParent biDirParent) throws EntityNotFoundException, BadEntityException {
//        try {
////            if (!isRootService(joinPoint)) {
////                log.debug("ignoring update advice, bc root service not called yet");
////                return;
////            }
//            if (((IdentifiableEntity) biDirParent).getId() != null) {
//                log.debug("detected update operation for BiDirParent: " + biDirParent + ", running preUpdateAdvice logic");
//                updateBiDirParentRelations(biDirParent);
//            } // else ignore, bc it is save operation not update
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private boolean isRootService(JoinPoint joinPoint) {
//        Class<?> userClass = ProxyUtils.getUserClass(joinPoint.getTarget());
        if (AopUtils.isAopProxy(joinPoint.getTarget()) || AopUtils.isCglibProxy(joinPoint.getTarget()) || Proxy.isProxyClass(joinPoint.getTarget().getClass()) || joinPoint.getTarget() instanceof AbstractServiceExtension) {
            return false;
        } else {
            return true;
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

        // todo link and unlink oldChild instead of new?
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
