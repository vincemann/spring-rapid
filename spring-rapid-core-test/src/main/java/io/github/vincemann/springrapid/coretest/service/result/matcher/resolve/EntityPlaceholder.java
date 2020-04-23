package io.github.vincemann.springrapid.coretest.service.result.matcher.resolve;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;

/**
 * Placeholder that gets resolved to {@link IdentifiableEntity}
 * at runtime by {@link EntityPlaceholderResolver}, after ServiceTest ran through.
 */
public enum EntityPlaceholder {
    DB_ENTITY,
    SERVICE_RETURNED_ENTITY,
    SERVICE_INPUT_ENTITY;
}
