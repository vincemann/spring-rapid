package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.testEntities;

import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirChildId;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirParentDto;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
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
