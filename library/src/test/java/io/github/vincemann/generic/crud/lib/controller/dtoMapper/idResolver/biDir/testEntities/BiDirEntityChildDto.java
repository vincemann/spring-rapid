package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.testEntities;

import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildDto;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentId;
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
