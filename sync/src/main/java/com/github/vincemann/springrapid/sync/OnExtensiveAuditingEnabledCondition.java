package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.sync.EnableExtensiveAuditing;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnExtensiveAuditingEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getBeanFactory().getBeansWithAnnotation(EnableExtensiveAuditing.class).size() > 0;
    }
}
