package com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.testEntities;


import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.autobidir.dto.parent.annotation.BiDirParentId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BiDirEntityChildDto extends IdentifiableEntityImpl<Long>  {

    @BiDirParentId(BiDirEntityParent.class)
    private Long entityPId;

    @BiDirParentId(BiDirSecondEntityParent.class)
    private Long secondEntityPId;
}
