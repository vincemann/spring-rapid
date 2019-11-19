package io.github.vincemann.generic.crud.lib.model.entityListener;

import io.github.vincemann.generic.crud.lib.model.entityListener.abs.BiDirEntityListener;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import io.github.vincemann.generic.crud.lib.service.finder.CrudServiceFinder;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;

//todo kann man @EntityListener annotation vllt wrappen in eigene die @Inherited ist und das an BiDirChildEntity ranmachen, dass der user nicht an die annotation denken muss?!
public class BiDirChildEntityListener extends BiDirEntityListener {

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
