package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.testEntities;

import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildId;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirDtoParent;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BiDirEntityParentDto extends IdentifiableEntityImpl<Long> implements BiDirDtoParent {

    @BiDirChildId(BiDirEntityChild.class)
    private Long entityChildId;
}
