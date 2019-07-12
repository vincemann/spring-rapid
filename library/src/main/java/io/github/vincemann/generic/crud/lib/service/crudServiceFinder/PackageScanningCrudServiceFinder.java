package io.github.vincemann.generic.crud.lib.service.crudServiceFinder;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class PackageScanningCrudServiceFinder implements CrudServiceFinder {
    private Map<Class<? extends IdentifiableEntity>,CrudService> entityClassCrudServiceMap = new HashMap<>();
    private final ApplicationContext applicationContext;


    public PackageScanningCrudServiceFinder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        scanFromApplicationContext();
    }

    private void scanFromApplicationContext(){
        applicationContext.getBeansOfType(CrudService.class).values()
                .forEach(crudService -> entityClassCrudServiceMap.put(crudService.getEntityClass(),crudService));
    }

    @Override
    public Map<Class<? extends IdentifiableEntity>,CrudService> getCrudServices() {
        return this.entityClassCrudServiceMap;
    }
}
