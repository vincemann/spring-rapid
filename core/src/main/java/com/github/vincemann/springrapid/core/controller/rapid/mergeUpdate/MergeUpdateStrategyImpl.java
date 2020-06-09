package com.github.vincemann.springrapid.core.controller.rapid.mergeUpdate;

import com.github.vincemann.springrapid.core.advice.log.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.config.ReflectionUtilsBean;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@Slf4j
public class MergeUpdateStrategyImpl implements MergeUpdateStrategy<IdentifiableEntity<?>> {

    @LogInteraction
    @Override
    public IdentifiableEntity<?> merge(IdentifiableEntity<?> patch, IdentifiableEntity<?> saved, Class<?> dtoClass) throws BadEntityException {
        Map<String, Field> entityFields = ReflectionUtilsBean.getInstance().getNameFieldMap(saved.getClass());
        Set<String> properties = ReflectionUtilsBean.getInstance().getProperties(dtoClass);
        for (String property : properties) {
            try {
                Field entityField = resolve(property, entityFields);
                if (entityField==null){
//                    throw new BadEntityException("Unknown Property: " +property);
                    log.warn("Dto property: " + property + " is not known in entity. skipping");
                    continue;
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
