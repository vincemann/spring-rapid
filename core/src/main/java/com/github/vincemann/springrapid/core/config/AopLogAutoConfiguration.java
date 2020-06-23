package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.aoplog.ProxyAwareAopLogger;
import com.github.vincemann.aoplog.HierarchicalAnnotationParser;
import com.github.vincemann.aoplog.ThreadAwareIndentingLogAdapter;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Set;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class AopLogAutoConfiguration {

    private static final boolean SKIP_NULL_FIELDS = true;
    private static final boolean FORCE_REFLECTION = true;
    private static final int CROP_THRESHOLD = 7;
    private static final Set<String> EXCLUDE_SECURE_FIELD_NAMES = Sets.newHashSet("password");

    @ConditionalOnMissingBean(ProxyAwareAopLogger.class)
    @Bean
    public ProxyAwareAopLogger aopLogger() {
        ProxyAwareAopLogger aopLogger = new ProxyAwareAopLogger(new HierarchicalAnnotationParser());
        aopLogger.setLogAdapter(new ThreadAwareIndentingLogAdapter(SKIP_NULL_FIELDS, CROP_THRESHOLD, EXCLUDE_SECURE_FIELD_NAMES,FORCE_REFLECTION));
        return aopLogger;
    }
}
