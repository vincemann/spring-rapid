package io.github.vincemann.generic.crud.lib.entityListener;

import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import io.github.vincemann.generic.crud.lib.service.crudServiceFinder.CrudServiceFinder;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import java.util.Collection;
import java.util.Set;

public class BiDirParentEntityListener extends BiDirEntityListener{

    public BiDirParentEntityListener(CrudServiceFinder crudServiceFinder) {
        super(crudServiceFinder);
    }

    @PreRemove
    public void onPreRemove(BiDirParent biDirParent) throws IllegalAccessException {
        biDirParent.dismissChildrensParent();
    }






    @PrePersist
    public void onPrePersist(BiDirParent biDirParent) throws IllegalAccessException {
        Set<? extends BiDirChild> children = biDirParent.getChildren();
        for (BiDirChild child : children) {
            child.findAndSetParent(biDirParent);
        }
        Set<Collection<? extends BiDirChild>> childCollections = biDirParent.getChildrenCollections().keySet();
        for (Collection<? extends BiDirChild> childCollection : childCollections) {
            for (BiDirChild biDirChild : childCollection) {
                biDirChild.findAndSetParent(biDirParent);
            }
        }
    }
}
