package com.github.vincemann.springrapid.coretest.automock;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;

/**
 * Automatically mocks all beans annotated with {@link ServiceComponent} or {@link Service}.
 * Used for controller testing -> use of @{@link org.springframework.boot.test.mock.mockito.MockBean} for service beans becomes needless.
 * Supports context caching because all service beans are mocked (otherwise for each diff subset of manually mocked beans with @{@link org.springframework.boot.test.mock.mockito.MockBean}
 * a new context gets created -> context cant be cached -> slow)
 */
@Slf4j
public class AutoMockBeanFactory extends DefaultListableBeanFactory {

    private HashMap<String,Object> mockedBeans = new HashMap<>();

    @Override
    protected Map<String, Object> findAutowireCandidates(final String beanName, final Class<?> requiredType, final DependencyDescriptor descriptor) {
        //log.debug("Trying to find Autowire Candidates for type: " +requiredType.getSimpleName()+ " , requesting bean is: " + beanName);
        String mockBeanName = Introspector.decapitalize(requiredType.getSimpleName()) + "Mock";
        Map<String, Object> autowireCandidates = new HashMap<>();
        try {
            autowireCandidates = super.findAutowireCandidates(beanName, requiredType, descriptor);
        } catch (UnsatisfiedDependencyException e) {
            if (e.getCause() != null && e.getCause().getCause() instanceof NoSuchBeanDefinitionException) {
                mockBeanName = ((NoSuchBeanDefinitionException) e.getCause().getCause()).getBeanName();
            }
            this.registerBeanDefinition(mockBeanName, BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition());
        }
        if (autowireCandidates.isEmpty()) {
            //todo @Repository auch automocken?
            //todo does this support @Qualifiers ? -> create diff mocks for @Autowired service and  @Secured @Autowired service ?
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
}
