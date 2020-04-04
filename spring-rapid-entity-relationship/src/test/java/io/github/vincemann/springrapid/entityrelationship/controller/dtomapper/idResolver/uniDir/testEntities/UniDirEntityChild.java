package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.uniDir.testEntities;

import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import io.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChild;
import io.github.vincemann.springrapid.entityrelationship.model.uniDir.parent.UniDirParentEntity;
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
