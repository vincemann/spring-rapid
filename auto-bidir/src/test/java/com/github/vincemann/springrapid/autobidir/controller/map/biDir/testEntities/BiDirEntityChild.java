package com.github.vincemann.springrapid.autobidir.controller.map.biDir.testEntities;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BiDirEntityChild extends IdentifiableEntityImpl<Long>   {

    @BiDirParentEntity
    private BiDirEntityParent biDirEntityParent;
    @BiDirParentEntity
    private BiDirSecondEntityParent biDirSecondEntityParent;
}
