package com.github.vincemann.springrapid.entityrelationship;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.dto.RelationalDtoType;

import java.util.Set;

public interface RelationalDtoManager {

    Set<RelationalDtoType> inferTypes(Class<IdentifiableEntity<?>> entityClass);

}
