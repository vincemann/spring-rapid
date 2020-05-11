package io.github.vincemann.springrapid.core.controller.rapid.mergeUpdate;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MergeUpdateStrategyImpl implements MergeUpdateStrategy<IdentifiableEntity<?>> {

    @Override
    public IdentifiableEntity<?> merge(IdentifiableEntity<?> patch, IdentifiableEntity<?> saved, Class<?> dtoClass) throws BadEntityException {
        Map<String, Field> entityFields = ReflectionUtils.getNonStaticFieldMap(saved.getClass());
        Set<String> properties = Arrays.stream(ReflectionUtils.getDeclaredFields(dtoClass, true))
                //ignore static fields
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(Field::getName)
                .collect(Collectors.toSet());
        for (String property : properties) {
            try {
                Field entityField = resolve(property, entityFields);
                if (entityField==null){
                    throw new BadEntityException("Unknown Property: " +property);
                }
                entityField.setAccessible(true);
                Object patchedValue = entityField.get(patch);
                entityField.set(saved, patchedValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return saved;
    }

    protected Field resolve(String property, Map<String, Field> entityFields) throws BadEntityException{
        return entityFields.get(property);
    }
}
