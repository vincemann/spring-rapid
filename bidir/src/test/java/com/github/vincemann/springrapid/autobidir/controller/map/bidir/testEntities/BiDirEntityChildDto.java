package com.github.vincemann.springrapid.autobidir.controller.map.bidir.testEntities;


import com.github.vincemann.springrapid.core.model.IdAwareEntityImpl;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.parent.BiDirParentId;

public class BiDirEntityChildDto extends IdAwareEntityImpl<Long> {

    @BiDirParentId(BiDirEntityParent.class)
    private Long entityPId;

    @BiDirParentId(BiDirSecondEntityParent.class)
    private Long secondEntityPId;

    public BiDirEntityChildDto() {
    }

    public Long getEntityPId() {
        return entityPId;
    }

    public void setEntityPId(Long entityPId) {
        this.entityPId = entityPId;
    }

    public Long getSecondEntityPId() {
        return secondEntityPId;
    }

    public void setSecondEntityPId(Long secondEntityPId) {
        this.secondEntityPId = secondEntityPId;
    }
}
