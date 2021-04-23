package com.github.vincemann.springrapid.entityrelationship.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.entityrelationship.model.BiDirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Aspect
@Slf4j

/**
 * Advice that keeps BiDirRelationships intact for Repo save operations (also update)
 */
public class BiDirEntitySaveAdvice extends BiDirEntityAdvice {


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public BiDirEntitySaveAdvice(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator);
    }

    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(entity)")
    public void prePersistBiDirEntity(BiDirEntity entity) throws BadEntityException, EntityNotFoundException, IllegalAccessException {

        if (BiDirParent.class.isAssignableFrom(entity.getClass())) {
            BiDirParent parent =(BiDirParent)entity;
            if (((IdentifiableEntity) entity).getId() == null) {
                //create
                log.debug("pre persist biDirParent hook reached for: " + parent);
                setChildrensParentRef(parent);
            } else {
                // update
                log.debug("pre update biDirParent hook reached for: " + parent);
                updateBiDirParentRelations(parent);
                // need to replace child here for partial update parent situation (replace detached child with session attached child (this))
                replaceChildrensParentRef(parent);
                // needs to be done to prevent detached error when adding parent to child via full update or save
                mergeParentsChildren(parent);
            }
        }



        if (BiDirChild.class.isAssignableFrom(entity.getClass())) {
            BiDirChild child =(BiDirChild)entity;
            if (((IdentifiableEntity) child).getId() == null) {
                //create
                log.debug("pre persist biDirChild hook reached for: " + child);
                setParentsChildRef(child);
            } else {
                // update
                log.debug("pre update biDirChild hook reached for: " + child);
                updateBiDirChildRelations(child);
                // need to replace child here for partial update parent situation (replace detached child with session attached child (this))
                replaceParentsChildRef(child);
                // needs to be done to prevent detached error when adding parent to child via full update or save
                mergeChildrensParents(child);
            }
        }
    }

//    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
//            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
//            "args(biDirChild)")
//    public void prePersistBiDiChild(BiDirChild biDirChild) throws BadEntityException, EntityNotFoundException, IllegalAccessException {
//
//    }

    private void mergeChildrensParents(BiDirChild biDirChild) {
        //--
        Set<Collection<BiDirParent>> parentCollections = biDirChild.findBiDirParentCollections().keySet();
        for (Collection<BiDirParent> parentCollection : parentCollections) {
            for (BiDirParent biDirParent : parentCollection) {
                entityManager.merge(biDirParent);
            }
        }
        //--
        //set backreferences
        for (BiDirParent parent : biDirChild.findSingleBiDirParents()) {
            entityManager.merge(parent);
        }
    }

    private void mergeParentsChildren(BiDirParent biDirParent) {
        Set<? extends BiDirChild> children = biDirParent.findSingleBiDirChildren();
        for (BiDirChild child : children) {
            entityManager.merge(child);
        }
        Set<Collection<BiDirChild>> childCollections = biDirParent.findBiDirChildCollections().keySet();
        for (Collection<BiDirChild> childCollection : childCollections) {
            for (BiDirChild biDirChild : childCollection) {
                entityManager.merge(biDirChild);
            }
        }
    }

    private void setChildrensParentRef(BiDirParent biDirParent) {
        Set<? extends BiDirChild> children = biDirParent.findSingleBiDirChildren();
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

        //--
        Set<Collection<BiDirParent>> parentCollections = biDirChild.findBiDirParentCollections().keySet();
        for (Collection<BiDirParent> parentCollection : parentCollections) {
            for (BiDirParent biDirParent : parentCollection) {
                biDirParent.unlinkBiDirChild(biDirChild);
                biDirParent.linkBiDirChild(biDirChild);
            }
        }
        //--

        for (BiDirParent parent : biDirChild.findSingleBiDirParents()) {
            // check if BiDirChild is present before
            parent.unlinkBiDirChild(biDirChild);
            parent.linkBiDirChild(biDirChild);
        }
    }

    private void replaceChildrensParentRef(BiDirParent biDirParent) {

        //--
        Set<Collection<BiDirChild>> childCollections = biDirParent.findBiDirChildCollections().keySet();
        for (Collection<BiDirChild> childCollection : childCollections) {
            for (BiDirChild biDirChild : childCollection) {
                biDirChild.unlinkBiDirParent(biDirParent);
                biDirChild.linkBiDirParent(biDirParent);
            }
        }
        //--
        //set backreferences
        for (BiDirChild child : biDirParent.findSingleBiDirChildren()) {
            // check if BiDirChild is present before
            child.unlinkBiDirParent(biDirParent);
            child.linkBiDirParent(biDirParent);
        }
    }

    private void setParentsChildRef(BiDirChild biDirChild) {
        //--
        Set<Collection<BiDirParent>> parentCollections = biDirChild.findBiDirParentCollections().keySet();
        for (Collection<BiDirParent> parentCollection : parentCollections) {
            for (BiDirParent biDirParent : parentCollection) {
                biDirParent.linkBiDirChild(biDirChild);
            }
        }
        //--
        //set backreferences
        for (BiDirParent parent : biDirChild.findSingleBiDirParents()) {
            parent.linkBiDirChild(biDirChild);
        }

    }


}
