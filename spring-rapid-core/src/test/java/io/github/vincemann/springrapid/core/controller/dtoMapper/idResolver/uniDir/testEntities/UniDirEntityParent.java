package io.github.vincemann.springrapid.core.controller.dtoMapper.idResolver.uniDir.testEntities;

import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import io.github.vincemann.springrapid.core.model.uniDir.child.UniDirChildEntity;
import io.github.vincemann.springrapid.core.model.uniDir.parent.UniDirParent;
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
