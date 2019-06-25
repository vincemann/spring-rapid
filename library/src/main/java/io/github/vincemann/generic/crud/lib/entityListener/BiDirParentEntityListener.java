package io.github.vincemann.generic.crud.lib.entityListener;

import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;

import javax.persistence.PreRemove;

public class BiDirParentEntityListener {

    @PreRemove
    public void onPreRemove(BiDirParent biDirParent) throws IllegalAccessException {
        biDirParent.dismissChildren();
    }
}
