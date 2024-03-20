package com.github.vincemann.springrapid.autobidir.controller.map.bidir.testEntities;

import com.github.vincemann.springrapid.core.model.IdAwareEntityImpl;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildEntity;


public class BiDirEntityParent extends IdAwareEntityImpl<Long> {

    @BiDirChildEntity
    private BiDirEntityChild biDIrEntityChild;

    public BiDirEntityParent() {
    }

    public BiDirEntityChild getBiDIrEntityChild() {
        return biDIrEntityChild;
    }

    public void setBiDIrEntityChild(BiDirEntityChild biDIrEntityChild) {
        this.biDIrEntityChild = biDIrEntityChild;
    }
}
