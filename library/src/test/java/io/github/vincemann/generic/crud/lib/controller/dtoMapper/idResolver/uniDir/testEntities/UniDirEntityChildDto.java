package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.testEntities;

import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirChildDto;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirParentId;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
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
