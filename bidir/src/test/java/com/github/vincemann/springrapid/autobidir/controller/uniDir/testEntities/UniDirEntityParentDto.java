package com.github.vincemann.springrapid.autobidir.controller.uniDir.testEntities;

import com.github.vincemann.springrapid.autobidir.resolveid.annotation.child.UniDirChildId;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UniDirEntityParentDto extends IdentifiableEntityImpl<Long>  {
    @UniDirChildId(UniDirEntityParentsChild.class)
    private Long childId;
}
