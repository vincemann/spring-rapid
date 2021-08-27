package com.github.vincemann.springrapid.entityrelationship;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.dto.RelationalDto;

import java.util.Set;

public interface RelationalDtoManager {

    Set<RelationalDto> inferTypes(Class<IdentifiableEntity<?>> entityClass);

}
