package com.github.vincemann.springrapid.core.service.locator;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.ServiceBeanType;

/**
 * API for finding a specific {@link CrudService}, that is in the Spring Container.
 * @see ServiceBeanType
 */
@ServiceComponent
public interface CrudServiceLocator {


    public CrudService find(Class<? extends IdentifiableEntity> entityClass, Class<? extends ServiceBeanType> annotation);
    //public CrudService find(String beanName);
    public CrudService find(Class<? extends IdentifiableEntity> entityClass);
    //public Map<ServiceBeanInfo, CrudService> find();
}
