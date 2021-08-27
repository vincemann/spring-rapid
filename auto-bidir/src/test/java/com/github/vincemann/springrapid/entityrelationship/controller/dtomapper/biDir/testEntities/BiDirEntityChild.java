package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.testEntities;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentEntity;
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
