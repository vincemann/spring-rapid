package io.github.vincemann.generic.crud.lib.entityListener;

import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;

import javax.persistence.PreRemove;

public class BiDirChildEntityListener {

    @PreRemove
    public void onPreRemove(BiDirChild biDirChild) throws IllegalAccessException {
        biDirChild.dismissParents();
    }
}
