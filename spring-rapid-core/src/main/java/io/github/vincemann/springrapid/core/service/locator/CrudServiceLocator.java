package io.github.vincemann.springrapid.core.service.locator;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.ServiceBeanType;

/**
 * Find all {@link CrudService}s in the Project mapped to the EntityClass handled by the CrudService
 */
@ServiceComponent
public interface CrudServiceLocator {


    public CrudService find(Class<? extends IdentifiableEntity> entityClass, Class<? extends ServiceBeanType> annotation);
    //public CrudService find(String beanName);
    public CrudService find(Class<? extends IdentifiableEntity> entityClass);
    //public Map<ServiceBeanInfo, CrudService> find();
}
