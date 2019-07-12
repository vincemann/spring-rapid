package io.github.vincemann.generic.crud.lib.controller.dtoMapper.backRefResolving;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChildEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class EntityParent extends IdentifiableEntityImpl<Long> implements BiDirParent {

    @BiDirChildEntity
    private EntityChild entityChild;

}
