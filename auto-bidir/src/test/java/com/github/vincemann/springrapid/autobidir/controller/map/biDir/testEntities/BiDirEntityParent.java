package com.github.vincemann.springrapid.autobidir.controller.map.biDir.testEntities;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BiDirEntityParent extends IdentifiableEntityImpl<Long> {

    @BiDirChildEntity
    private BiDirEntityChild biDIrEntityChild;

}
