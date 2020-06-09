package com.github.vincemann.springrapid.demo.service.jpa;

import com.github.vincemann.springrapid.demo.model.Visit;
import com.github.vincemann.springrapid.demo.repo.VisitRepository;
import com.github.vincemann.springrapid.demo.service.VisitService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@ServiceComponent
public class VisitJPAService extends JPACrudService<Visit,Long, VisitRepository> implements VisitService {
}
