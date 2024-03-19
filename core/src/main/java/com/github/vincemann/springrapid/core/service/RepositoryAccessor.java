package com.github.vincemann.springrapid.core.service;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.ResolvableType;
import org.springframework.data.repository.CrudRepository;


public class RepositoryAccessor implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <T, ID> CrudRepository<T, ID> getRepositoryForEntityClass(Class<T> entityClass) {
        // Iterate over all beans of type CrudRepository
        String[] repoBeanNames = applicationContext.getBeanNamesForType(CrudRepository.class);
        for (String beanName : repoBeanNames) {
            CrudRepository<?, ?> repository = (CrudRepository<?, ?>) applicationContext.getBean(beanName);
            ResolvableType resolvableType = ResolvableType.forClass(repository.getClass()).as(CrudRepository.class);
            Class<?> resolvedEntityClass = resolvableType.getGeneric(0).resolve();

            // Check if the resolved entity class matches the provided entity class
            if (entityClass.equals(resolvedEntityClass)) {
                return (CrudRepository<T, ID>) repository;
            }
        }
        throw new IllegalStateException("No CrudRepository found for " + entityClass.getName());
    }
}
