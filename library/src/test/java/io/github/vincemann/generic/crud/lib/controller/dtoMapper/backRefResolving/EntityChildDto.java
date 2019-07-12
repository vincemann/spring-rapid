package io.github.vincemann.generic.crud.lib.controller.dtoMapper.backRefResolving;

import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirDtoChild;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class EntityChildDto extends IdentifiableEntityImpl<Long> implements BiDirDtoChild {

    @BiDirParentId(EntityParent.class)
    private Long entityPId;

    @BiDirParentId(SecondEntityParent.class)
    private Long secondEntityPId;
}
