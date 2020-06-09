package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.testEntities;

import com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildDto;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BiDirEntityChildDto extends IdentifiableEntityImpl<Long> implements BiDirChildDto {

    @BiDirParentId(BiDirEntityParent.class)
    private Long entityPId;

    @BiDirParentId(BiDirSecondEntityParent.class)
    private Long secondEntityPId;
}
