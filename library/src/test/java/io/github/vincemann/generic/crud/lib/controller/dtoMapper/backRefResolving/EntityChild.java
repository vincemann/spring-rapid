package io.github.vincemann.generic.crud.lib.controller.dtoMapper.backRefResolving;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class EntityChild extends IdentifiableEntityImpl<Long>  implements BiDirChild {

    @BiDirParentEntity
    private EntityParent entityParent;
    @BiDirParentEntity
    private SecondEntityParent secondEntityParent;
}
