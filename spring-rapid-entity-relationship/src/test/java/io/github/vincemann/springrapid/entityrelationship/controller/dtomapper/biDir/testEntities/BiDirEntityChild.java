package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.testEntities;

import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.child.BiDirChild;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParentEntity;
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
