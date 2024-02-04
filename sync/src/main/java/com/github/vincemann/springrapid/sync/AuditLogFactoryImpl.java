package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.sync.model.AuditId;
import com.github.vincemann.springrapid.sync.model.AuditLog;
import com.github.vincemann.springrapid.sync.model.EntityDtoMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuditLogFactoryImpl implements AuditLogFactory {

    private EntityMappingCollector entityMappingCollector;


    @Override
    public AuditLog create(AuditId id) {
        AuditLog auditLog = new AuditLog(id);

        Set<EntityDtoMapping> dtoMappings = new HashSet<>();


        for (Class<?> dtoClass : findMappingsForEntity(id.getConvertedEntityClass())) {
            EntityDtoMapping mapping = EntityDtoMapping.builder()
                    .auditLog(auditLog)
                    .lastUpdateTime(LocalDateTime.now())
                    .dtoClass(dtoClass.getName())
                    .build();
            dtoMappings.add(mapping);
        }

        auditLog.setDtoMappings(dtoMappings);
        return auditLog;
    }

    protected Set<Class<?>> findMappingsForEntity(Class<?> entityClass){
        Map<Class<?>, Set<Class<?>>> entityDtoMappings = entityMappingCollector.getEntityToDtoMappings();
        Set<Class<?>> dtoClasses = entityDtoMappings.get(entityClass);
        if (dtoClasses == null){
            throw new IllegalArgumentException("No dto classes for entity class: " + entityClass + " found. \n " +
                    "Make sure to annotate dto classes of entity with @EntityMapping and configure basePackages in rapid-sync properties");
        }
        return dtoClasses;
    }

    @Autowired
    public void setEntityMappingCollector(EntityMappingCollector entityMappingCollector) {
        this.entityMappingCollector = entityMappingCollector;
    }
}
