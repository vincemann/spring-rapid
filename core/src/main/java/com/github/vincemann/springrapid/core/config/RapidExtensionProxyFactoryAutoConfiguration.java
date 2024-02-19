package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.proxy.annotation.AnnotationExtensionProxyFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class RapidExtensionProxyFactoryAutoConfiguration {

    @Bean
    public AnnotationExtensionProxyFactory annotationExtensionProxyFactory(){
        return new AnnotationExtensionProxyFactory();
    }
}
