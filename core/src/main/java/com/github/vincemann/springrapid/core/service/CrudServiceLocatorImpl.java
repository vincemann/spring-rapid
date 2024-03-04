package com.github.vincemann.springrapid.core.service;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.util.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
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
public class CrudServiceLocatorImpl implements CrudServiceLocator,
        ApplicationListener<ContextRefreshedEvent>,
        BeanFactoryAware
{
    /**
     * contains services that either have @{@link com.github.vincemann.springrapid.core.Root} qualifier or are primary.
     */
    @Getter
    private Map<Class<? extends IdentifiableEntity>, CrudService> rootServices = new HashMap<>();

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
        List<String> crudServiceNames = Lists.newArrayList(beanFactory.getBeanNamesForType(CrudService.class));

        Map<Class<? extends CrudService>, List<CrudService>> nonPrimaryServices = new HashMap<>();
        for (String beanName : crudServiceNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            CrudService bean = ((CrudService) beanFactory.getBean(beanName));
            if (beanDefinition.isPrimary()) {
                // if primary add to root services
                rootServices.put(bean.getEntityClass(), bean);
            } else {
                // if not primary add to non root services
                List<CrudService> services = nonPrimaryServices.get(bean.getClass());
                if (services == null) {
                    nonPrimaryServices.put(bean.getClass(), Lists.newArrayList(bean));
                } else {
                    services.add(bean);
                }
            }
        }
        // services that are not primary but are annotated with @Root are root services
        Map<Class<?>,CrudService> addedRootServices = new HashMap<>();
        Map<String, CrudService> rootQualifierServices = BeanFactoryAnnotationUtils.qualifiedBeansOfType(beanFactory, CrudService.class, "root");
        for (CrudService service : rootQualifierServices.values()){
            // check if there is already primary bean
            if (rootServices.get(service.getEntityClass()) != null) {
                continue;
            }
            // check if multiple beans for same entity class are marked as root
            CrudService alreadyAdded = addedRootServices.get(service.getEntityClass());
            if (alreadyAdded != null){
                throw new IllegalArgumentException("cannot mark multiple beans for same entity class as @root and not define primary one");
            }
            // all checks passed, add as root service bean
            addedRootServices.put(service.getEntityClass(),service);
            rootServices.put(service.getEntityClass(),service);
        }

        //services that were not primary nor marked as @root but only exist once, are practically root services -> get added to map as well
        for (Map.Entry<Class<? extends CrudService>, List<CrudService>> entry : nonPrimaryServices.entrySet()) {
            List<CrudService> services = entry.getValue();
            if (services.size() == 1) {
                CrudService service = services.get(0);
                if (rootServices.get(service.getEntityClass()) != null) {
                    continue;
                }
                rootServices.put(service.getEntityClass(), service);
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
        return rootServices.get(entityClass);
    }
}
