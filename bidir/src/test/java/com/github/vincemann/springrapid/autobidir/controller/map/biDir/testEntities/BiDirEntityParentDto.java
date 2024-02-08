package com.github.vincemann.springrapid.autobidir.controller.map.biDir.testEntities;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildId;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BiDirEntityParentDto extends IdentifiableEntityImpl<Long>  {

    @BiDirChildId(BiDirEntityChild.class)
    private Long entityChildId;
}
