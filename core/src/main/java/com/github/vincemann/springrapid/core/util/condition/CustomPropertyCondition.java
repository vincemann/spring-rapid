package com.github.vincemann.springrapid.core.util.condition;

import com.github.vincemann.springrapid.core.util.condition.ConditionalOnCustomProperties;
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

            for (String propertyName : propertyNames) {
                String propertyValue = context.getEnvironment().getProperty(propertyName);

                // Return true if the property is missing or its value is "true"
                if (propertyValue != null && propertyValue.equals("false")) {
                    return false;
                }
            }
        }

        return true;
    }
}
