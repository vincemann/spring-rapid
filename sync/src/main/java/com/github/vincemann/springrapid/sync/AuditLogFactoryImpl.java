package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
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
    public AuditLog create(IdentifiableEntity entity) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(entity.getId().toString());
        auditLog.setEntityClass(entity.getClass().getName());

        Set<EntityDtoMapping> dtoMappings = new HashSet<>();


        for (Class<?> dtoClass : findMappingsForEntity(entity)) {
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

    protected Set<Class<?>> findMappingsForEntity(IdentifiableEntity entity){
        Map<Class<?>, Set<Class<?>>> entityDtoMappings = entityMappingCollector.getEntityToDtoMappings();
        Set<Class<?>> dtoClasses = entityDtoMappings.get(entity.getClass());
        if (dtoClasses == null){
            throw new IllegalArgumentException("No dto classes for entity class: " + entity.getClass() + " found. \n " +
                    "Make sure to annotate dto classes of entity with @EntityMapping and configure basePackages in rapid-sync properties");
        }
        return dtoClasses;
    }

    @Autowired
    public void setEntityMappingCollector(EntityMappingCollector entityMappingCollector) {
        this.entityMappingCollector = entityMappingCollector;
    }
}
