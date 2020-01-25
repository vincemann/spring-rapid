package io.github.vincemann.generic.crud.lib.jpaAuditing.entityListener;

import io.github.vincemann.generic.crud.lib.model.biDir.child.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParent;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;

/**
 * Manages bidirectional relationship of Child side.
 * Works only in conjunction with {@link io.github.vincemann.generic.crud.lib.service.plugin.BiDirChildPlugin}.
 */
public class BiDirChildEntityListener{


    @PreRemove
    public void onPreRemove(BiDirChild biDirChild) throws IllegalAccessException {
        for(BiDirParent parent: biDirChild.findParents()){
            parent.dismissChild(biDirChild);
        }
        biDirChild.dismissParents();
    }

    @PrePersist
    public void onPrePersist(BiDirChild biDirChild) throws IllegalAccessException {
        //set backreferences
        for(BiDirParent parent: biDirChild.findParents()){
            parent.addChild(biDirChild);
        }
    }

}
