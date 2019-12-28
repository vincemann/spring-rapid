package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.model.Visit;
import io.github.vincemann.demo.repositories.VisitRepository;
import io.github.vincemann.demo.service.VisitService;
import io.github.vincemann.generic.crud.lib.service.jpa.JPACrudService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("springdatajpa")
public class VisitJPAService extends JPACrudService<Visit,Long, VisitRepository> implements VisitService {
}
