package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.testEntities;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.model.biDir.child.BiDirChildEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BiDirSecondEntityParent extends IdentifiableEntityImpl<Long> implements BiDirParent {

    @BiDirChildEntity
    private BiDirEntityChild biDIrEntityChild;
}
