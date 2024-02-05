package com.github.vincemann.springrapid.core.model.audit;

import com.github.vincemann.springrapid.core.service.id.IdConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

@Slf4j
public class AuditTemplateImpl implements AuditTemplate {

    private AuditorAware auditorAware;
    private IdConverter idConverter;
    @Override
    public void updateLastModified(AuditingEntity entity) {
        entity.setLastModifiedDate(new Date());
        Optional<Object> currentAuditor = auditorAware.getCurrentAuditor();
        if (currentAuditor.isPresent())
            entity.setLastModifiedById(((Serializable) currentAuditor.get()));
        else{
            log.warn("Could not find any auditor, setting to unknown id");
            entity.setLastModifiedById(idConverter.getUnknownId());
        }
    }


    @Override
    public void updateLastModified(AuditingEntity entity, Serializable auditorId){
        entity.setLastModifiedDate(new Date());
        entity.setLastModifiedById(auditorId);
    }

    @Autowired
    public void setAuditorAware(AuditorAware auditorAware) {
        this.auditorAware = auditorAware;
    }

    @Autowired
    public void setIdConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }
}
