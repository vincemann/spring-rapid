package com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UniDirEntityParent extends IdentifiableEntityImpl<Long> implements UniDirParent {
    @UniDirChildEntity
    private UniDirEntityParentsChild entityChild;
}
