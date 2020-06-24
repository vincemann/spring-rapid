package com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities;

import com.github.vincemann.springrapid.entityrelationship.dto.child.UniDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.UniDirParentId;
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
