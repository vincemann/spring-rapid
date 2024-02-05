package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.Toy;
import com.github.vincemann.springrapid.syncdemo.repo.ToyRepository;
import com.github.vincemann.springrapid.syncdemo.service.ToyService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
@EnableAutoBiDir
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
