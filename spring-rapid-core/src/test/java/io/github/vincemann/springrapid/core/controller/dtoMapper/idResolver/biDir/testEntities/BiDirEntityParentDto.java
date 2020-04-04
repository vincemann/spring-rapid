package io.github.vincemann.springrapid.core.controller.dtoMapper.idResolver.biDir.testEntities;

import io.github.vincemann.springrapid.core.dto.biDir.BiDirChildId;
import io.github.vincemann.springrapid.core.dto.biDir.BiDirParentDto;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BiDirEntityParentDto extends IdentifiableEntityImpl<Long> implements BiDirParentDto {

    @BiDirChildId(BiDirEntityChild.class)
    private Long entityChildId;
}
