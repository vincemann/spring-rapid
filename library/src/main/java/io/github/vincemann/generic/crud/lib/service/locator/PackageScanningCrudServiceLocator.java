package io.github.vincemann.generic.crud.lib.service.locator;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PackageScanningCrudServiceLocator implements CrudServiceLocator, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
    private Map<Class<? extends IdentifiableEntity>,CrudService> entityClassCrudServiceMap = new HashMap<>();
    private ApplicationContext applicationContext;


    private void scanFromApplicationContext(ApplicationContext applicationContext){
        applicationContext.getBeansOfType(CrudService.class).values()
                .forEach(crudService -> entityClassCrudServiceMap.put(crudService.getEntityClass(),crudService));
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //all beans are initialized -> now is the right time to scan for beans
        scanFromApplicationContext(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Map<Class<? extends IdentifiableEntity>,CrudService> find() {
        return this.entityClassCrudServiceMap;
    }
}
