package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare.resolve;

/**
 * Placeholder that gets resolved to {@link io.github.vincemann.generic.crud.lib.model.IdentifiableEntity}
 * at runtime by {@link CompareEntityPlaceholderResolver}, after ServiceTest ran through.
 */
public enum CompareEntityPlaceholder {
    DB_ENTITY,
    SERVICE_RETURNED_ENTITY,
    SERVICE_INPUT_ENTITY;
}
