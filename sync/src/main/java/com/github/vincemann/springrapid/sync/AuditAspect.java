package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.sync.repo.AuditLogRepository;
import com.github.vincemann.springrapid.sync.repo.EntityDtoMappingRepository;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class AuditAspect {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private EntityDtoMappingRepository entityDtoMappingRepository;




}
