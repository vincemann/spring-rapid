package io.github.vincemann.generic.crud.lib.service.locator;

import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.ServiceBeanType;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Find all {@link CrudService}s in the Project mapped to the EntityClass handled by the CrudService
 */
@ServiceComponent
public interface CrudServiceLocator {


    public List<CrudService> find(Class serviceClass, Class<? extends ServiceBeanType>... annotations);
    public Optional<CrudService> find(String beanName);
    public List<CrudService> find(Class serviceClass);
    public Map<ServiceBeanInfo, CrudService> find();
}
