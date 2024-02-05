package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * API for finding specific {@link CrudService}s, that are in the current Spring Container.
 * you can use qualifier annotations to differentiate between service versions of same type.
 */

public interface CrudServiceLocator {

    public CrudService find(Class<? extends IdentifiableEntity> entityClass, Class<? extends Annotation> annotation);
    //public CrudService find(String beanName);
    public CrudService find(Class<? extends IdentifiableEntity> entityClass);

    public void loadPrimaryServices();
}
