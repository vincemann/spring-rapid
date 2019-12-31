package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.testEntities;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.model.biDir.child.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BiDirEntityChild extends IdentifiableEntityImpl<Long>  implements BiDirChild {

    @BiDirParentEntity
    private BiDirEntityParent biDirEntityParent;
    @BiDirParentEntity
    private BiDirSecondEntityParent biDirSecondEntityParent;
}
