package io.github.vincemann.springrapid.core.test.service.result.matcher.compare.resolve;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;

/**
 * Placeholder that gets resolved to {@link IdentifiableEntity}
 * at runtime by {@link CompareEntityPlaceholderResolver}, after ServiceTest ran through.
 */
public enum CompareEntityPlaceholder {
    DB_ENTITY,
    SERVICE_RETURNED_ENTITY,
    SERVICE_INPUT_ENTITY;
}
