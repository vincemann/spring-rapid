package io.github.vincemann.springrapid.core.test.automock;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Service;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

@Slf4j
public class AutoMockBeanFactory extends DefaultListableBeanFactory {

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
            if(requiredType.isAnnotationPresent(ServiceComponent.class)
                    || requiredType.isAnnotationPresent(Service.class)) {
                final Object mock = mock(requiredType);
                System.err.println("Automocking bean with name " + mockBeanName + "of type: " + requiredType.getSimpleName());
                autowireCandidates.put(mockBeanName, mock);
                this.addSingleton(mockBeanName, mock);
            }
        }
        return autowireCandidates;
    }
}
