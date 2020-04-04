package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.uniDir.testEntities;

import io.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirChildDto;
import io.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirParentId;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
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
