package com.github.vincemann.springrapid.core.controller.mergeUpdate;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.config.ReflectionUtilsBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

@Slf4j
@Setter
@Getter
public class MergeUpdateStrategyImpl implements MergeUpdateStrategy {
    private boolean strict = false;

    ////@LogInteraction
    @Override
    public <E extends IdentifiableEntity<?>> E merge(E patch, E saved, Class<?> dtoClass) {
        ReflectionUtils.doWithFields(dtoClass,dtoField -> {
            if (!Modifier.isStatic(dtoField.getModifiers())) {
                Class<? extends IdentifiableEntity> entityClass = patch.getClass();
                String propertyName = transform(dtoField.getName());

                Field entityField = ReflectionUtils.findField(entityClass, propertyName);
                if (entityField == null) {
                    if (strict) {
                        throw new IllegalArgumentException("Unknown Property: " + propertyName);
                    } else {
                        log.warn("Dto property: " + propertyName + " is not known in entity. skipping");
                        return;
                    }
                }
                ReflectionUtils.makeAccessible(entityField);
                Object patchedValue = entityField.get(patch);
                entityField.set(saved, patchedValue);
            }
        });
        return saved;
    }

    protected String transform(String propertyName){
        return propertyName;
    }
}
