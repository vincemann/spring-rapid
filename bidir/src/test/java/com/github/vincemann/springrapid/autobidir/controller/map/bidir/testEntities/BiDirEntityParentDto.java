package com.github.vincemann.springrapid.autobidir.controller.map.bidir.testEntities;

import com.github.vincemann.springrapid.autobidir.resolveid.annotation.child.BiDirChildId;

import com.github.vincemann.springrapid.core.model.IdAwareEntityImpl;

public class BiDirEntityParentDto extends IdAwareEntityImpl<Long> {

    @BiDirChildId(BiDirEntityChild.class)
    private Long entityChildId;

    public BiDirEntityParentDto() {
    }

    public Long getEntityChildId() {
        return entityChildId;
    }

    public void setEntityChildId(Long entityChildId) {
        this.entityChildId = entityChildId;
    }
}
