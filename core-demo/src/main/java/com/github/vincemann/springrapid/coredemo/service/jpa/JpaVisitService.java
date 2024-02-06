package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.coredemo.model.Visit;
import com.github.vincemann.springrapid.coredemo.repo.VisitRepository;
import com.github.vincemann.springrapid.coredemo.service.VisitService;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@EnableAutoBiDir
public class JpaVisitService
        extends JpaCrudService<Visit,Long, VisitRepository>
                implements VisitService {
    @Override
    public Class<?> getTargetClass() {
        return JpaVisitService.class;
    }
}
