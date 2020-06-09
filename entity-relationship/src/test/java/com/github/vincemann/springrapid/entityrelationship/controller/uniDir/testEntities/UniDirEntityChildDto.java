package com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities;

import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirParentId;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UniDirEntityChildDto extends IdentifiableEntityImpl<Long> implements UniDirChildDto {
    @UniDirParentId(UniDirEntityChildsParent.class)
    private Long parentId;
}
