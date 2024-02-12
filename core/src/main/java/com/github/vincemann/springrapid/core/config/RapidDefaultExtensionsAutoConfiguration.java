package com.github.vincemann.springrapid.core.config;


import com.github.vincemann.springrapid.core.DefaultExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxy;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Configuration
public class RapidDefaultExtensionsAutoConfiguration {

    @Autowired
    ApplicationContext context;




    @PostConstruct
    public void init() {
        // Discover and process all ServiceExtensions marked with @DefaultExtension
        Map<String, Object> defaultExtensions = context.getBeansWithAnnotation(DefaultExtension.class);
        defaultExtensions.values().forEach(extension -> {
            DefaultExtension annotation = extension.getClass().getAnnotation(DefaultExtension.class);
            applyExtensionToQualifiedBeans(findQualifier(annotation), obtainFreshInstance((ServiceExtension) extension));
        });
    }

    private void applyExtensionToQualifiedBeans(String qualifier, ServiceExtension extension) {
        // Dynamically find and process all beans with the specified qualifier
        Map<String, Object> beansWithQualifier = BeanFactoryAnnotationUtils.qualifiedBeansOfType(context, Object.class, qualifier);
        beansWithQualifier.values().forEach(bean -> {
            ExtensionProxy proxy = ProxyUtils.getExtensionProxy(bean);
            if (proxy != null && proxy.getDefaultExtensionsEnabled() && !proxy.isIgnored(extension.getClass())) {
                log.debug("Adding default extension to proxy: " + extension);
                proxy.addExtension(extension);
            }
        });
    }

    public String findQualifier(DefaultExtension annotation){
        Qualifier qualifier = AnnotationUtils.findAnnotation(annotation.qualifier(), Qualifier.class);
        Assert.notNull(qualifier,"default extensions 'qualifier' field needs to be set to annotation type having @Qualifier meta annotation");
        return qualifier.value();
    }

    /**
     * scope of extensions has to be Prototype, because I need a new instance for each proxy of that extension.
     * Call this method for each proxy to get a new set of instances of the default extensions.
     */
    private ServiceExtension obtainFreshInstance(ServiceExtension extension){
        String beanName = context.getBeanNamesForType(extension.getClass())[0];
        // get new instance
        return  (ServiceExtension) context.getBean(beanName);
    }

}
