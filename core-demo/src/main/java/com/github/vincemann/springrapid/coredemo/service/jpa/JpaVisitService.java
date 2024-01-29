package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.coredemo.model.Visit;
import com.github.vincemann.springrapid.coredemo.repo.VisitRepository;
import com.github.vincemann.springrapid.coredemo.service.VisitService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@Component
public class JpaVisitService extends JPACrudService<Visit,Long, VisitRepository> implements VisitService, TargetClassAware {
    @Override
    public Class<?> getTargetClass() {
        return JpaVisitService.class;
    }
}
