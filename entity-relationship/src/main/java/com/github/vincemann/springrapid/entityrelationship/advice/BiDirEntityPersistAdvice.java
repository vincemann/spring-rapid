package com.github.vincemann.springrapid.entityrelationship.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Collection;
import java.util.Set;

@Aspect
@Slf4j
/**
 * Advice that keeps BiDirRelationships intact for {@link com.github.vincemann.springrapid.core.service.CrudService#save(IdentifiableEntity)} - operations.
 */
public class BiDirEntityPersistAdvice {

    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(biDirParent)")
    public void prePersistBiDirParent(BiDirParent biDirParent) {
        log.debug("pre persist biDirParent hook reached for: " + biDirParent);
        setChildrensParentRef(biDirParent);
    }

    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(biDirChild)")
    public void prePersistBiDiChild(BiDirChild biDirChild) {
        log.debug("pre persist biDirChild hook reached for: " + biDirChild);
        setParentsChildRef(biDirChild);
    }

    private void setChildrensParentRef(BiDirParent biDirParent){
        Set<? extends BiDirChild> children = biDirParent.findBiDirSingleChildren();
        for (BiDirChild child : children) {
            child.addBiDirParent(biDirParent);
        }
        Set<Collection<BiDirChild>> childCollections = biDirParent.findAllBiDirChildCollections().keySet();
        for (Collection<BiDirChild> childCollection : childCollections) {
            for (BiDirChild biDirChild : childCollection) {
                biDirChild.addBiDirParent(biDirParent);
            }
        }
    }


    private void setParentsChildRef(BiDirChild biDirChild) {
        //set backreferences
        for (BiDirParent parent : biDirChild.findBiDirParents()) {
            parent.addBiDirChild(biDirChild);
        }
    }
}
