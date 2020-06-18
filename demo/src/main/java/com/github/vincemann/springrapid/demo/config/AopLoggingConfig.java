package com.github.vincemann.springrapid.demo.config;

import com.github.vincemann.springrapid.log.nickvl.AOPLogger;
import com.github.vincemann.springrapid.log.nickvl.UniversalLogAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Collections;
import java.util.Set;

//@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
public class AopLoggingConfig {

    private static final boolean SKIP_NULL_FIELDS = true;
    private static final int CROP_THRESHOLD = 7;
    private static final Set<String> EXCLUDE_SECURE_FIELD_NAMES = Collections.<String>emptySet();

//    @Bean
//    public AOPLogger aopLogger() {
//        AOPLogger aopLogger = new AOPLogger();
//        aopLogger.setLogAdapter(new UniversalLogAdapter(SKIP_NULL_FIELDS, CROP_THRESHOLD, EXCLUDE_SECURE_FIELD_NAMES));
//        return aopLogger;
//    }

}
