package com.github.vincemann.springrapid.autobidir.controller.uniDir.testEntities;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.UniDirChildEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UniDirEntityParent extends IdentifiableEntityImpl<Long>  {
    @UniDirChildEntity
    private UniDirEntityParentsChild entityChild;
}
