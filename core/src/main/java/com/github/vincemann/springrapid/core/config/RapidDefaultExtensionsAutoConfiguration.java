package com.github.vincemann.springrapid.core.config;


import com.github.vincemann.springrapid.core.DefaultExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxy;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
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
            Class<?> targetClass = null;

            if (AopUtils.isAopProxy(extension)) {
                // If the bean is a proxy, determine the target class
                targetClass = AopProxyUtils.ultimateTargetClass(extension);
            } else {
                // If it's not a proxy, just use its class
                targetClass = extension.getClass();
            }

            DefaultExtension annotation = targetClass.getAnnotation(DefaultExtension.class);
            Assert.notNull(annotation,"no DefaultExtension annotation defined on the extension class: " + targetClass);
            applyExtensionToQualifiedBeans(findQualifier(annotation), obtainFreshInstance((ServiceExtension) extension));
        });
    }

    private void applyExtensionToQualifiedBeans(String qualifier, ServiceExtension extension) {
        // Dynamically find and process all beans with the specified qualifier
        Map<String, Object> beansWithQualifier = BeanFactoryAnnotationUtils.qualifiedBeansOfType((ListableBeanFactory) context.getAutowireCapableBeanFactory(), Object.class, qualifier);
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
     * scope of extensions is often Prototype ( whenever state is present within extension ) -> this method fetches new instance
     */
    private ServiceExtension obtainFreshInstance(ServiceExtension extension){
        String beanName = context.getBeanNamesForType(extension.getClass())[0];
        // get new instance
        return  (ServiceExtension) context.getBean(beanName);
    }

}
