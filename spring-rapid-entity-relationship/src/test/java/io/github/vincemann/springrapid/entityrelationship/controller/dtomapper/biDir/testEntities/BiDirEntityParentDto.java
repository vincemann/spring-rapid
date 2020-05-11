package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.testEntities;

import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildId;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentDto;
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
