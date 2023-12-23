package com.github.vincemann.springrapid.core.util;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Condition;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OtherConfigPresentCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String otherConfigClassName = metadata.getAnnotationAttributes(OtherConfigPresentCondition.class.getName()).get("value").toString();

        try {
            Class<?> otherConfigClass = Class.forName(otherConfigClassName);
            return !context.getBeanFactory().containsBean(otherConfigClass.getSimpleName());
        } catch (ClassNotFoundException e) {
            return true; // If the class is not found, consider the condition met
        }
    }
}
