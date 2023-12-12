package com.github.vincemann.springrapid.core.util;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

public class CustomPropertyCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(ConditionalOnCustomProperties.class.getName());

        if (attributes != null) {
            String[] propertyNames = (String[]) attributes.getFirst("properties");
            boolean allMatch = true;

            for (String propertyName : propertyNames) {
                String propertyValue = context.getEnvironment().getProperty(propertyName);

                if (propertyValue == null || !propertyValue.equals("true")) {
                    allMatch = false;
                    break;
                }
            }

            return allMatch;
        }

        return false;
    }
}
