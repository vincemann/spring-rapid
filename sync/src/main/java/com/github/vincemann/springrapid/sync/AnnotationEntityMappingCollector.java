package com.github.vincemann.springrapid.sync;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnnotationEntityMappingCollector implements EntityMappingCollector, ApplicationListener<ContextRefreshedEvent> {


    private Map<Class<?>, Set<Class<?>>> entityToDtoMappings = new HashMap<>();
    private SyncProperties syncProperties;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        collectEntityToDtoMappings();
    }

    @Override
    public void collectEntityToDtoMappings() {
        // Define a filter to search for classes annotated with @EntityMapping
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(EntityMapping.class));

        // Scan the classpath for candidate components
        for (String basePackage : syncProperties.getBasePackages()) {
            for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
                try {
                    Class<?> dtoClass = Class.forName(beanDefinition.getBeanClassName());
                    EntityMapping mapping = dtoClass.getAnnotation(EntityMapping.class);
                    Class<?> entityClass = mapping.value();

                    // Populate the map
                    entityToDtoMappings.computeIfAbsent(entityClass, k -> new HashSet<>()).add(dtoClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Map<Class<?>, Set<Class<?>>> getEntityToDtoMappings() {
        if (entityToDtoMappings == null)
            throw new IllegalArgumentException("entity dto mappings must be collected first via this.collectEntityToDtoMappings()");
        return entityToDtoMappings;
    }

    @Autowired
    public void setSyncProperties(SyncProperties syncProperties) {
        this.syncProperties = syncProperties;
    }
}
