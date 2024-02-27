package com.github.vincemann.springrapid.core.service;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.util.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utilizes {@link ConfigurableListableBeanFactory} to find the {@link com.github.vincemann.springrapid.core.service.CrudService} of a given type
 *
 */
@Slf4j
public class CrudServiceLocatorImpl implements CrudServiceLocator, ApplicationListener<ContextRefreshedEvent>, BeanFactoryAware {
    @Getter
    private Map<Class<? extends IdentifiableEntity>, CrudService> primaryServices = new HashMap<>();

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadServices();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory= (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void loadServices() {
        List<String> beanNames = Lists.newArrayList(beanFactory.getBeanNamesForType(CrudService.class));

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


    //@LogInteraction
    @Override
    public CrudService find(Class<? extends IdentifiableEntity> entityClass, Class<? extends Annotation> annotation) {
        Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(annotation);
        List result = beansWithAnnotation.values().stream()
                .filter(b -> ((CrudService) b).getEntityClass().equals(entityClass))
                .collect(Collectors.toList());
        Assert.isTrue(result.size() == 1, "Found multiple service beans with annotation: " + annotation + " of type: " + entityClass);
        return ((CrudService) result.get(0));
    }

    //@LogInteraction
    @Override
    public CrudService find(Class<? extends IdentifiableEntity> entityClass) {
        return primaryServices.get(entityClass);
    }
}
