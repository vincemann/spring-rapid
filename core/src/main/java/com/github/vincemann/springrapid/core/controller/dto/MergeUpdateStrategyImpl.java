package com.github.vincemann.springrapid.core.controller.dto;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class MergeUpdateStrategyImpl implements MergeUpdateStrategy {


    /**
     * Copies all field values from {@param patch} to {@param saved}, that are present in {@param dtoClass}.
     * Allows transformation of field name via {@link #transform(String)}.
     * So dtos field name and target field name in entity class might differ.
     *
     * @param patch patch entity, containing all updated values
     * @param saved saved entity, representing current managed state of entity to update
     * @param dtoClass dtoClass used for current update operation
     * @return merged entity (saved)
     * @param <E> entity type
     */
    @Override
    public <E extends IdentifiableEntity<?>> E merge(E patch, E saved, Class<?> dtoClass) {
        ReflectionUtils.doWithFields(dtoClass, dtoField -> {
            Class<? extends IdentifiableEntity> entityClass = patch.getClass();
            String propertyName = transform(dtoField.getName());

            Field entityField = ReflectionUtils.findField(entityClass, propertyName);
            // managed by Controller, cant happen
            Assert.notNull(entityField, "Unknown Update Property: " + propertyName);
            ReflectionUtils.makeAccessible(entityField);
            Object patchedValue = entityField.get(patch);
            entityField.set(saved, patchedValue);

        }, ReflectionUtils.COPYABLE_FIELDS);
        return saved;
    }

    protected String transform(String propertyName) {
        return propertyName;
    }
}
