package io.github.vincemann.generic.crud.lib.service.crudServiceFinder;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.*;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.AssignableTypeFilter;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class PackageScanningCrudServiceFinder implements CrudServiceFinder, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
    private Map<Class<? extends IdentifiableEntity>,CrudService> entityClassCrudServiceMap = new HashMap<>();
    private ApplicationContext applicationContext;


    private void scanFromApplicationContext(ApplicationContext applicationContext){
        applicationContext.getBeansOfType(CrudService.class).values()
                .forEach(crudService -> entityClassCrudServiceMap.put(crudService.getEntityClass(),crudService));
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //all beans are intialized -> now is the right time to scan for beans
        scanFromApplicationContext(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Map<Class<? extends IdentifiableEntity>,CrudService> getCrudServices() {
        return this.entityClassCrudServiceMap;
    }
}
