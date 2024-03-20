package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.coredemo.model.Toy;
import com.github.vincemann.springrapid.coredemo.repo.ToyRepository;
import com.github.vincemann.springrapid.coredemo.service.ToyService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
@EnableAutoBiDir
public class JpaToyService
        extends AbstractCrudService<Toy,Long, ToyRepository>
                implements ToyService {

    @Override
    public Optional<Toy> findByName(String name) {
        return getRepository().findByName(name);
    }

}
