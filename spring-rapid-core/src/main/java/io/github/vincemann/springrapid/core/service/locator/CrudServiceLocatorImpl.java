package io.github.vincemann.springrapid.core.service.locator;

import io.github.vincemann.springrapid.core.advice.log.LogInteraction;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.ServiceBeanType;
import lombok.extern.slf4j.Slf4j;
import io.github.vincemann.springrapid.core.util.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilizes {@link ConfigurableListableBeanFactory} to find the {@link CrudService} in question.
 * @see ServiceBeanType
 */
@Slf4j
public class CrudServiceLocatorImpl implements CrudServiceLocator, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, BeanDefinitionRegistryPostProcessor {
    private Map<Class<? extends IdentifiableEntity>,CrudService> entityClassPrimaryServiceMap = new HashMap<>();
    private ApplicationContext applicationContext;

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    private void loadPrimaryServices(ConfigurableListableBeanFactory beanFactory){
        String[] beanNames = beanFactory.getBeanNamesForType(CrudService.class);
        Map<Class<? extends CrudService>,List<CrudService>> nonPrimaryServiceClassBeanMap = new HashMap<>();
        for (String beanName : beanNames) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            CrudService bean = ((CrudService) beanFactory.getBean(beanName));
            if (bd.isPrimary()) {
                entityClassPrimaryServiceMap.put(bean.getEntityClass(),bean);
            }else {
                List<CrudService> crudServices = nonPrimaryServiceClassBeanMap.get(bean.getClass());
                if(crudServices==null) {
                    nonPrimaryServiceClassBeanMap.put(bean.getClass(), Lists.newArrayList(bean));
                }else {
                    crudServices.add(bean);
                }
            }
        }
        //services that were not primary but only exist once, are practically primary -> get added to map as well
        for (Map.Entry<Class<? extends CrudService>, List<CrudService>> classBeansEntry : nonPrimaryServiceClassBeanMap.entrySet()) {
            if(classBeansEntry.getValue().size()==1){
                CrudService service = classBeansEntry.getValue().get(0);
                if(entityClassPrimaryServiceMap.get(service.getEntityClass())!=null){
                    continue;
                }
                entityClassPrimaryServiceMap.put(service.getEntityClass(),service);
            }
        }
    }
//
//    private List<Class<? extends Annotation>> getBeanTypeAnnotations(CrudService service){
//        log.debug("Service: " + service);
//        Assert.notNull(service,"Found Service Bean was null");
//        Class serviceClass  = AopProxyUtils.ultimateTargetClass(service);
//        log.debug("Services annotations: " + Arrays.stream(serviceClass.getDeclaredAnnotations())
//                .map(Annotation::annotationType)
//                .collect(Collectors.toList())
//        );
//        Annotation[] annotations = serviceClass.getDeclaredAnnotations();
//        if(annotations==null){
//            return new ArrayList<>();
//        }
//        List<Class<? extends Annotation>> result = new ArrayList<>();
//        for (Annotation annotation : annotations) {
//            if(annotation.annotationType().isAnnotationPresent(ServiceBeanType.class)){
//                result.add(annotation.annotationType());
//            }
//        }
//        return result;
//    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //all beans are initialized -> now is the right time to scan for beans
        loadPrimaryServices(beanFactory);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

//    @Override
//    public synchronized Map<ServiceBeanInfo, CrudService> find() {
//        return infoCrudServiceMap;
//    }

    @LogInteraction
    @Override
    public synchronized CrudService find(Class<? extends IdentifiableEntity> entityClass, Class<? extends ServiceBeanType> annotation) {
//        return infoCrudServiceMap.entrySet().stream()
//                .filter(e -> e.getKey().getEntityClass().equals(serviceClass)
//                        && e.getKey().getBeanTypeAnnotations().equals(Lists.newArrayList(annotations)))
//                .map(Map.Entry::getValue)
//                .collect(Collectors.toList());
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(annotation);
        List result = beansWithAnnotation.values().stream()
                .filter(b -> ((CrudService)b).getEntityClass().equals(entityClass))
                .collect(Collectors.toList());
        Assert.isTrue(result.size()==1,"Found multiple service beans with annotation: "+ annotation +" of type: " + entityClass);
        return ((CrudService) result.get(0));
    }

//    @Override
//    public synchronized CrudService find(String beanName) {
//        return (CrudService) applicationContext.getBean(beanName);
//    }

    @LogInteraction
    @Override
    public synchronized CrudService find(Class<? extends IdentifiableEntity> entityClass) {
//        return infoCrudServiceMap.entrySet().stream()
//                .filter(e -> e.getKey().getEntityClass().equals(serviceClass))
//                .map(Map.Entry::getValue)
//                .collect(Collectors.toList());
        return entityClassPrimaryServiceMap.get(entityClass);
    }
}
