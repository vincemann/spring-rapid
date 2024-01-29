package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.proxy.annotation.AnnotationExtensionProxyFactory;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.context.annotation.Bean;

@ServiceConfig
public class RapidProxyAutoConfiguration {

    @Bean
    public AnnotationExtensionProxyFactory annotationExtensionProxyFactory(){
        return new AnnotationExtensionProxyFactory();
    }
}
