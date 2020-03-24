package io.github.vincemann.generic.crud.lib.advice;

import io.github.vincemann.generic.crud.lib.model.biDir.child.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Aspect
@Transactional
@Component
public class BiDirRelationshipAdvice {




    @Before(/*"io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.inServiceLayer() && " +*/
            "io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.saveOperation() && " +
            "args(biDirParent)")
    public void prePersistBiDirParent(BiDirParent biDirParent) throws IllegalAccessException {
        setChildrensParentRef(biDirParent);
    }

    @Before(/*"io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.inServiceLayer() && " +*/
            "io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.saveOperation() && "+
            "args(biDirChild)")
    public void prePersistBiDiChild(BiDirChild biDirChild) throws IllegalAccessException {
        setParentsChildRef(biDirChild);
    }


    @Before(/*"io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.inServiceLayer() && " +*/
            "io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.deleteOperation() && "+
            "args(biDirParent)")
    public void preRemoveBiDirParent(BiDirParent biDirParent) throws IllegalAccessException {
        biDirParent.dismissChildrensParent();
    }

    @Before(/*"io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.inServiceLayer() && " +*/
            "io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.deleteOperation() && "+
            "args(biDirChild)")
    public void preRemoveBiDirChild(BiDirChild biDirChild) throws IllegalAccessException {
        for(BiDirParent parent: biDirChild.findParents()){
            parent.dismissChild(biDirChild);
        }
        biDirChild.dismissParents();
    }


    private void setChildrensParentRef(BiDirParent biDirParent) throws IllegalAccessException {
        Set<? extends BiDirChild> children = biDirParent.getChildren();
        for (BiDirChild child : children) {
            child.setParentRef(biDirParent);
        }
        Set<Collection<? extends BiDirChild>> childCollections = biDirParent.getChildrenCollections().keySet();
        for (Collection<? extends BiDirChild> childCollection : childCollections) {
            for (BiDirChild biDirChild : childCollection) {
                biDirChild.setParentRef(biDirParent);
            }
        }
    }


    private void setParentsChildRef(BiDirChild biDirChild) throws IllegalAccessException {
        //set backreferences
        for(BiDirParent parent: biDirChild.findParents()){
            parent.addChild(biDirChild);
        }
    }

}
