package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.testEntities;

import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildDto;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentId;
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
