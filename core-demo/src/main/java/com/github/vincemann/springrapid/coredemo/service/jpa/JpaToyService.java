package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.Toy;
import com.github.vincemann.springrapid.coredemo.repo.ToyRepository;
import com.github.vincemann.springrapid.coredemo.service.ToyService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
@Component
public class JpaToyService extends JPACrudService<Toy,Long, ToyRepository> implements ToyService {

    @Override
    public Optional<Toy> findByName(String name) {
        return getRepository().findByName(name);
    }

    @Override
    public Class<?> getTargetClass() {
        return JpaToyService.class;
    }
}
