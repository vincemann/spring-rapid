package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.testEntities;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.model.uniDir.UniDirChild;
import io.github.vincemann.generic.crud.lib.model.uniDir.UniDirParentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UniDirEntityChild extends IdentifiableEntityImpl<Long> implements UniDirChild {
    @UniDirParentEntity
    private UniDirEntityChildsParent uniDirEntityChildsParent;
}
