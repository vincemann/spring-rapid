package io.github.vincemann.springrapid.core.controller.dtoMapper.idResolver.biDir.testEntities;

import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import io.github.vincemann.springrapid.core.model.biDir.child.BiDirChildEntity;
import io.github.vincemann.springrapid.core.model.biDir.parent.BiDirParent;
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
