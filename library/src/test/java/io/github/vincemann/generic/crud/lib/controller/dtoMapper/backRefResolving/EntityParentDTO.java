package io.github.vincemann.generic.crud.lib.controller.dtoMapper.backRefResolving;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class EntityParentDTO extends IdentifiableEntityImpl<Long> {

    private EntityChildDto entityChild;
}
