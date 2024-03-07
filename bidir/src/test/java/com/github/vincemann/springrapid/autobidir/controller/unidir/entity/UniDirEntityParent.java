package com.github.vincemann.springrapid.autobidir.controller.unidir.entity;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.UniDirChildEntity;


public class UniDirEntityParent extends IdentifiableEntityImpl<Long>  {
    @UniDirChildEntity
    private UniDirEntityParentsChild entityChild;

    public UniDirEntityParent() {
    }

    public UniDirEntityParentsChild getEntityChild() {
        return entityChild;
    }

    public void setEntityChild(UniDirEntityParentsChild entityChild) {
        this.entityChild = entityChild;
    }
}
