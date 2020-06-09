package com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities;

import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirParentDto;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UniDirEntityParentDto extends IdentifiableEntityImpl<Long> implements UniDirParentDto {
    @UniDirChildId(UniDirEntityParentsChild.class)
    private Long childId;
}
