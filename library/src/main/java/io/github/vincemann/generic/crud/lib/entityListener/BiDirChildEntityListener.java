package io.github.vincemann.generic.crud.lib.entityListener;

import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import io.github.vincemann.generic.crud.lib.service.crudServiceFinder.CrudServiceFinder;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;

public class BiDirChildEntityListener extends BiDirEntityListener{

    public BiDirChildEntityListener(CrudServiceFinder crudServiceFinder) {
        super(crudServiceFinder);
    }

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
