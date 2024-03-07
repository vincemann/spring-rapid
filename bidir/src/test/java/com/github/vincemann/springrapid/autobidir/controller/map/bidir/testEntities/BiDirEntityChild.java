package com.github.vincemann.springrapid.autobidir.controller.map.bidir.testEntities;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;


public class BiDirEntityChild extends IdentifiableEntityImpl<Long>   {

    @BiDirParentEntity
    private BiDirEntityParent biDirEntityParent;
    @BiDirParentEntity
    private BiDirSecondEntityParent biDirSecondEntityParent;

    public BiDirEntityChild() {
    }

    public BiDirEntityParent getBiDirEntityParent() {
        return biDirEntityParent;
    }

    public void setBiDirEntityParent(BiDirEntityParent biDirEntityParent) {
        this.biDirEntityParent = biDirEntityParent;
    }

    public BiDirSecondEntityParent getBiDirSecondEntityParent() {
        return biDirSecondEntityParent;
    }

    public void setBiDirSecondEntityParent(BiDirSecondEntityParent biDirSecondEntityParent) {
        this.biDirSecondEntityParent = biDirSecondEntityParent;
    }
}
