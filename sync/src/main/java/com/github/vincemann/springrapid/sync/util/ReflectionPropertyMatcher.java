package com.github.vincemann.springrapid.sync.util;

import org.springframework.cache.annotation.Cacheable;

import java.util.Set;

public class ReflectionPropertyMatcher {

    @Cacheable(value = "matchingPropertiesCache", key = "{#clazz.getName(), #propertyNames}")
    public boolean hasMatchingPropertyValue(Class<?> clazz, Set<String> propertyNames) {
        final boolean[] hasMatch = {false}; // Use an array to modify from inside the lambda

        org.springframework.util.ReflectionUtils.doWithFields(clazz, field -> {
            if (propertyNames.contains(field.getName())){
                hasMatch[0] = true;
            }
        }, org.springframework.util.ReflectionUtils.COPYABLE_FIELDS);
        return hasMatch[0];
    }

}
