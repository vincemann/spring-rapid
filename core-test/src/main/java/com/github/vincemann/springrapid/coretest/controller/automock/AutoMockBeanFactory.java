package com.github.vincemann.springrapid.coretest.controller.automock;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.beans.Introspector;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Automatically mocks all beans annotated with {@link ServiceComponent} or {@link Service}.
 * Used for controller testing -> use of @{@link org.springframework.boot.test.mock.mockito.MockBean} for service beans becomes obsolete.
 * Just @{@link org.springframework.beans.factory.annotation.Autowire} service beans, and you will get mocks.
 * Supports context caching because all service beans are mocked (otherwise for each diff subset of manually mocked beans with @{@link org.springframework.boot.test.mock.mockito.MockBean}
 * a new context gets created -> context cant be cached -> slow)
 */
@Slf4j
public class AutoMockBeanFactory extends DefaultListableBeanFactory {

    private HashMap<String,Object> mockedBeans = new HashMap<>();

    @Override
    protected Map<String, Object> findAutowireCandidates(final String beanName, final Class<?> requiredType, final DependencyDescriptor descriptor) {
        //log.debug("Trying to find Autowire Candidates for type: " +requiredType.getSimpleName()+ " , requesting bean is: " + beanName);

        String bn = beanName;
        Map<String, Object> autowireCandidates = new HashMap<>();
        try {
            autowireCandidates = super.findAutowireCandidates(bn, requiredType, descriptor);
        } catch (UnsatisfiedDependencyException e) {
            if (e.getCause() != null && e.getCause().getCause() instanceof NoSuchBeanDefinitionException) {
                bn = ((NoSuchBeanDefinitionException) e.getCause().getCause()).getBeanName();
            }
            this.registerBeanDefinition(bn, BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition());
        }
        if (autowireCandidates.isEmpty()) {
            String mockBeanName = createMockedBeanName(requiredType,descriptor);
            //todo @Repository auch automocken?
            if(requiredType.isAnnotationPresent(ServiceComponent.class)
                    || requiredType.isAnnotationPresent(Service.class)) {
                // prevent double mock creation of same singleton bean requested multiple times
                Object mock = mockedBeans.get(mockBeanName);
                if (mock==null){
                    log.info("Automocking bean with name " + mockBeanName + "of type: " + requiredType.getSimpleName());
                    mock =  mock(requiredType);
                }
                autowireCandidates.put(mockBeanName, mock);
                mockedBeans.put(mockBeanName,mock);
                this.addSingleton(mockBeanName, mock);
            }
        }
        return autowireCandidates;
    }

    /**
     * Takes @{@link org.springframework.beans.factory.annotation.Qualifier}s on method and constructor injection into consideration.
     * -> separate mock creation for diff @Qualifiers
     */
    protected String createMockedBeanName(Class<?> type, DependencyDescriptor dependencyDescriptor){
        String name = Introspector.decapitalize(type.getSimpleName()) + "Mock";
        MethodParameter methodParameter = dependencyDescriptor.getMethodParameter();
        if (methodParameter==null){
            return name;
        }
        Executable executable = methodParameter.getExecutable();
        Qualifier qualifier = AnnotationUtils.findAnnotation(executable, Qualifier.class);
        if (qualifier!=null){
            name=qualifier.value().concat(StringUtils.capitalize(name));
        }
        return name;
    }

    public void resetMocks(){
        for (Object mock : mockedBeans.values()) {
            Mockito.reset(mock);
        }
    }
}
