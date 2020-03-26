package io.github.vincemann.generic.crud.lib.service.locator;

import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.ServiceBeanType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.util.ProxyUtils;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class PackageScanningCrudServiceLocator implements CrudServiceLocator, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
    private Map<ServiceBeanInfo,CrudService> infoCrudServiceMap = new HashMap<>();
    private ApplicationContext applicationContext;


    private void scanFromApplicationContext(ApplicationContext applicationContext){
        for (Map.Entry<String, CrudService> nameCrudServiceEntry : applicationContext.getBeansOfType(CrudService.class).entrySet()) {
            infoCrudServiceMap.put(ServiceBeanInfo.builder()
                    .beanTypeAnnotations(
                            Lists.newArrayList(AopProxyUtils.ultimateTargetClass(nameCrudServiceEntry.getValue()).getAnnotations())
                                    .stream()
                                    .filter(annotation -> {
                                        //klappt alles nicht... ich bekomme die annotation nicht bzw deren type, immer nur sun proxy
                                        try {
                                            return Proxy.getInvocationHandler(annotation).getClass()
                                                    .isAnnotationPresent(ServiceBeanType.class);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                                    .map(Annotation::getClass)
                                    .collect(Collectors.toList())
                    )
                    .name(nameCrudServiceEntry.getKey())
                    .entityClass(nameCrudServiceEntry.getValue().getEntityClass())
                    .build(),

                    nameCrudServiceEntry.getValue());
        }
    }
    @SuppressWarnings({"unchecked"})
    protected <T> T getTargetObject(Object proxy) throws Exception {
        while( (AopUtils.isJdkDynamicProxy(proxy))) {
            return (T) getTargetObject(((Advised)proxy).getTargetSource().getTarget());
        }
        return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
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
    public synchronized Map<ServiceBeanInfo, CrudService> find() {
        return infoCrudServiceMap;
    }

    @Override
    public synchronized List<CrudService> find(Class serviceClass, Class<? extends ServiceBeanType>... annotations) {
        return infoCrudServiceMap.entrySet().stream()
                .filter(e -> e.getKey().getEntityClass().equals(serviceClass)
                        && e.getKey().getBeanTypeAnnotations().equals(Lists.newArrayList(annotations)))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized Optional<CrudService> find(String beanName) {
        List<CrudService> result = infoCrudServiceMap.entrySet().stream()
                .filter(e -> e.getKey().getName().equals(beanName))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        if (result.size()>1){
            throw new IllegalArgumentException("found multiple beans for the same name");
        }
        else if(result.size()==1){
            return Optional.of(result.get(0));
        }else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized List<CrudService> find(Class serviceClass) {
        return infoCrudServiceMap.entrySet().stream()
                .filter(e -> e.getKey().getEntityClass().equals(serviceClass))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
