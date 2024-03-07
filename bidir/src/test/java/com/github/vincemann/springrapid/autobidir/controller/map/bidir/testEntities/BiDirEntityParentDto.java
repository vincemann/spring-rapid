package com.github.vincemann.springrapid.autobidir.controller.map.bidir.testEntities;

import com.github.vincemann.springrapid.autobidir.resolveid.annotation.child.BiDirChildId;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

public class BiDirEntityParentDto extends IdentifiableEntityImpl<Long>  {

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
