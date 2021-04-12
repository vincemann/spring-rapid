package com.github.vincemann.springrapid.entityrelationship.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;

import java.util.Collection;
import java.util.Set;

@Aspect
@Slf4j
// order is important, save must be before update
//@Order(1)
/**
 * Advice that keeps BiDirRelationships intact for Repo save operations (also update)
 */
public class BiDirEntitySaveAdvice {

    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirParent)")
    public void prePersistBiDirParent(BiDirParent biDirParent) {
//        if(((IdentifiableEntity) biDirParent).getId()==null) {
            log.debug("pre persist biDirParent hook reached for: " + biDirParent);
            setChildrensParentRef(biDirParent);
//        }
    }

    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirChild)")
    public void prePersistBiDiChild(BiDirChild biDirChild) {
        if(((IdentifiableEntity) biDirChild).getId()==null) {
            log.debug("pre persist biDirChild hook reached for: " + biDirChild);
            setParentsChildRef(biDirChild);
        }else {
            // need to replace child here for update parent situation (replace detached child with session attached child (this))
            replaceParentsChildRef(biDirChild);
        }
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

    private void replaceParentsChildRef(BiDirChild biDirChild) {
        //set backreferences
        for (BiDirParent parent : biDirChild.findBiDirParents()) {
            parent.dismissBiDirChild(biDirChild);
            parent.addBiDirChild(biDirChild);
        }
    }

    private void setParentsChildRef(BiDirChild biDirChild) {
        //set backreferences
        for (BiDirParent parent : biDirChild.findBiDirParents()) {
            parent.addBiDirChild(biDirChild);
        }
    }
}
