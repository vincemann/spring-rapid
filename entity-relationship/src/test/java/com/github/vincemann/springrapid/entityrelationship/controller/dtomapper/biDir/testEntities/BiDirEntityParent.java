package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.testEntities;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.model.biDir.child.BiDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BiDirEntityParent extends IdentifiableEntityImpl<Long> implements BiDirParent {

    @BiDirChildEntity
    private BiDirEntityChild biDIrEntityChild;

}
