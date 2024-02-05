package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.syncdemo.model.Visit;
import com.github.vincemann.springrapid.syncdemo.repo.VisitRepository;
import com.github.vincemann.springrapid.syncdemo.service.VisitService;
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
