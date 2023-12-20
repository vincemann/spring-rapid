package com.github.vincemann.springrapid.core.controller;


import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

@Slf4j
@Setter
@Getter
public class MergeUpdateStrategyImpl implements MergeUpdateStrategy {

    private CoreProperties coreProperties;

    ////@LogInteraction
    @Override
    public <E extends IdentifiableEntity<?>> E merge(E patch, E saved, Class<?> dtoClass) throws BadEntityException {
        try{
            ReflectionUtils.doWithFields(dtoClass,dtoField -> {
                if (!com.github.vincemann.springrapid.core.util.ReflectionUtils.isStaticOrInnerField(dtoField)) {
                    Class<? extends IdentifiableEntity> entityClass = patch.getClass();
                    String propertyName = transform(dtoField.getName());

                    Field entityField = ReflectionUtils.findField(entityClass, propertyName);
                    // managed by Controller, cant happen
                    if (entityField == null){
                        // cant happen
                        throw new IllegalArgumentException("Unknown Update Property: " + propertyName);
                    }
//                    if (entityField == null) {
//                        if (coreProperties.getController().isStrictUpdateMerge()) {
//                            //gets translated to checked exception below
//                            throw new IllegalArgumentException("Unknown Update Property: " + propertyName);
//                        } else {
//                            log.warn("Dto property: " + propertyName + " is not known in entity. skipping");
//                            return;
//                        }
//                    }
                    ReflectionUtils.makeAccessible(entityField);
                    Object patchedValue = entityField.get(patch);
                    entityField.set(saved, patchedValue);
                }
            });
            //I have to do this exception disaster bc i cant throw checked exceptions in lambda expressions
        }catch (IllegalArgumentException e){
            throw new BadEntityException(e);
        }

        return saved;
    }

    protected String transform(String propertyName){
        return propertyName;
    }

    @Autowired
    public void injectCoreProperties(CoreProperties coreProperties) {
        this.coreProperties = coreProperties;
    }
}
