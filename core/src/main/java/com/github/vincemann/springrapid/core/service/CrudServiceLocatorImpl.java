package com.github.vincemann.springrapid.core.service;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.github.vincemann.springrapid.core.util.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilizes {@link ConfigurableListableBeanFactory} to find the {@link com.github.vincemann.springrapid.core.service.CrudService} of a given type
 *
 */
@Slf4j
public class CrudServiceLocatorImpl implements CrudServiceLocator, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, BeanFactoryPostProcessor {
    @Getter
    private Map<Class<? extends IdentifiableEntity>, CrudService> primaryServices = new HashMap<>();
    private ApplicationContext applicationContext;

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadPrimaryServices();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void loadPrimaryServices() {
        List<String> beanNames = Lists.newArrayList(beanFactory.getBeanNamesForType(CrudService.class));
        List<String> extensionNames = Lists.newArrayList(beanFactory.getBeanNamesForType(BasicServiceExtension.class));

        //skip extensions
        beanNames.removeAll(extensionNames);

        Map<Class<? extends CrudService>, List<CrudService>> nonPrimaryServices = new HashMap<>();
        for (String beanName : beanNames) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            CrudService bean = ((CrudService) beanFactory.getBean(beanName));
            if (bd.isPrimary()) {
                primaryServices.put(bean.getEntityClass(), bean);
            } else {
                List<CrudService> crudServices = nonPrimaryServices.get(bean.getClass());
                if (crudServices == null) {
                    nonPrimaryServices.put(bean.getClass(), Lists.newArrayList(bean));
                } else {
                    crudServices.add(bean);
                }
            }
        }
        //services that were not primary but only exist once, are practically primary -> get added to map as well
        for (Map.Entry<Class<? extends CrudService>, List<CrudService>> classBeansEntry : nonPrimaryServices.entrySet()) {
            if (classBeansEntry.getValue().size() == 1) {
                CrudService service = classBeansEntry.getValue().get(0);
                if (primaryServices.get(service.getEntityClass()) != null) {
                    continue;
                }
                primaryServices.put(service.getEntityClass(), service);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    //@LogInteraction
    @Override
    public synchronized CrudService find(Class<? extends IdentifiableEntity> entityClass, Class<? extends Annotation> annotation) {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(annotation);
        List result = beansWithAnnotation.values().stream()
                .filter(b -> ((CrudService) b).getEntityClass().equals(entityClass))
                .collect(Collectors.toList());
        Assert.isTrue(result.size() == 1, "Found multiple service beans with annotation: " + annotation + " of type: " + entityClass);
        return ((CrudService) result.get(0));
    }

    //@LogInteraction
    @Override
    public synchronized CrudService find(Class<? extends IdentifiableEntity> entityClass) {
        return primaryServices.get(entityClass);
    }
}
