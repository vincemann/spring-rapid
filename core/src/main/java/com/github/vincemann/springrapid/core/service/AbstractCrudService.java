package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;


public abstract class AbstractCrudService
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E, Id>
                >
        implements CrudService<E, Id>, ApplicationContextAware {

    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) GenericTypeResolver.resolveTypeArguments(this.getClass(),CrudService.class)[0];
    private R repository;
    @Setter
    protected CrudService<E, Id> service; // root version of service


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.service = provideRootService(applicationContext);
    }


    /**
     * Overwrite this to inject different root version
     */
    protected CrudService<E,Id> provideRootService(ApplicationContext applicationContext){
        // this works - gives me proxied instance with aop working
        return applicationContext.getBean(this.getClass());
    }

    public R getRepository() {
        return repository;
    }

    @Autowired
    public void setRepository(R repository) {
        this.repository = repository;
    }

    @Override
    public Class<E> getEntityClass() {
        return entityClass;
    }

}