package io.github.vincemann.generic.crud.lib.service.locator;

import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.ServiceBeanType;
import org.springframework.data.annotation.CreatedDate;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
