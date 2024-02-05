package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.sync.AuditLogFactory;
import com.github.vincemann.springrapid.sync.model.AuditId;
import com.github.vincemann.springrapid.sync.model.AuditLog;
import com.github.vincemann.springrapid.sync.model.EntityDtoMapping;
import com.github.vincemann.springrapid.sync.repo.AuditLogRepository;
import com.github.vincemann.springrapid.sync.util.ReflectionPropertyMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JpaAuditLogService implements AuditLogService {

    private AuditLogRepository auditLogRepository;
    private ReflectionPropertyMatcher propertyMatcher;
    private AuditLogFactory auditLogFactory;


    @Transactional
    @Override
    public void updateAuditLog(AuditId id, Set<String> properties) {
        AuditLog auditLog = findOrCreateAuditLog(id);
        Set<EntityDtoMapping> matchingMappings = findMatchingMappings(auditLog,properties);
        updateMappingsTimestamp(matchingMappings);
    }

    @Transactional
    @Override
    public void updateAuditLog(AuditId id){
        AuditLog auditLog = findOrCreateAuditLog(id);
        updateMappingsTimestamp(auditLog.getDtoMappings());
    }

    @Override
    public void deleteById(AuditId id) {
        auditLogRepository.deleteById(id);
    }

    @Override
    public AuditLog findOrCreateAuditLog(AuditId id){
        Optional<AuditLog> auditLogById = auditLogRepository.findById(id);
        if (auditLogById.isEmpty()){
            // create
            AuditLog auditLog = auditLogFactory.create(id);
            return auditLogRepository.save(auditLog);
        }else{
            return auditLogById.get();
        }
    }

    protected Set<EntityDtoMapping> findMatchingMappings(AuditLog auditLog, Set<String> properties) {
        return auditLog.getDtoMappings().stream().filter(mapping -> {
            try {
                return propertyMatcher.hasMatchingPropertyValue(Class.forName(mapping.getDtoClass()),properties);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("could not find dto class for name: " + mapping.getDtoClass(), e);
            }
        }).collect(Collectors.toSet());
    }


    protected void updateMappingsTimestamp(Set<EntityDtoMapping> mappings){
        mappings.forEach(mapping -> mapping.setLastUpdateTime(LocalDateTime.now()));
    }

    protected AuditId getId(IdentifiableEntity entity){
        return new AuditId(entity.getClass().getName(),entity.getId().toString());
    }

    @Autowired
    public void setAuditLogRepository(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Autowired
    public void setPropertyMatcher(ReflectionPropertyMatcher propertyMatcher) {
        this.propertyMatcher = propertyMatcher;
    }

    @Autowired
    public void setAuditLogFactory(AuditLogFactory auditLogFactory) {
        this.auditLogFactory = auditLogFactory;
    }
}
