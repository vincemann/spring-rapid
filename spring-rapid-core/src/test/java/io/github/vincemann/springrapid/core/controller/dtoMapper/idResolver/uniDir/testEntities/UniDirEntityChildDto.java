package io.github.vincemann.springrapid.core.controller.dtoMapper.idResolver.uniDir.testEntities;

import io.github.vincemann.springrapid.core.dto.uniDir.UniDirChildDto;
import io.github.vincemann.springrapid.core.dto.uniDir.UniDirParentId;
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
