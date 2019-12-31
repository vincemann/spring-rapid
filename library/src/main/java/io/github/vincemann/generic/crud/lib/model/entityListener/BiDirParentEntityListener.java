package io.github.vincemann.generic.crud.lib.model.entityListener;

import io.github.vincemann.generic.crud.lib.model.biDir.child.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParent;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import java.util.Collection;
import java.util.Set;

public class BiDirParentEntityListener {


    @PreRemove
    public void onPreRemove(BiDirParent biDirParent) throws IllegalAccessException {
        biDirParent.dismissChildrensParent();
    }

    @PrePersist
    public void onPrePersist(BiDirParent biDirParent) throws IllegalAccessException {
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
}
