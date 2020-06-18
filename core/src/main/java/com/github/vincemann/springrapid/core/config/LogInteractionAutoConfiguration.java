package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.log.nickvl.AOPLogger;
import com.github.vincemann.springrapid.log.nickvl.HierarchicalAnnotationParser;
import com.github.vincemann.springrapid.log.nickvl.ThreadAwareIndentingLogAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Collections;
import java.util.Set;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class LogInteractionAutoConfiguration {

    private static final boolean SKIP_NULL_FIELDS = true;
    private static final int CROP_THRESHOLD = 7;
    private static final Set<String> EXCLUDE_SECURE_FIELD_NAMES = Collections.<String>emptySet();

    @ConditionalOnMissingBean(AOPLogger.class)
    @Bean
    public AOPLogger aopLogger() {
        AOPLogger aopLogger = new AOPLogger(new HierarchicalAnnotationParser());
        aopLogger.setLogAdapter(new ThreadAwareIndentingLogAdapter(SKIP_NULL_FIELDS, CROP_THRESHOLD, EXCLUDE_SECURE_FIELD_NAMES));
        return aopLogger;
    }
}
