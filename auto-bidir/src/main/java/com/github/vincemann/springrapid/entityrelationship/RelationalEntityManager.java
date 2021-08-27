package com.github.vincemann.springrapid.entityrelationship;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.dto.RelationalDto;
import com.github.vincemann.springrapid.entityrelationship.model.RelationalEntity;

import java.util.Set;

public interface RelationalEntityManager {

    Set<RelationalEntity> inferTypes(Class<? extends IdentifiableEntity> entityClass);
}
