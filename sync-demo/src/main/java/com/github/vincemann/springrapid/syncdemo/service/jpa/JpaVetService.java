package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.syncdemo.model.Vet;
import com.github.vincemann.springrapid.syncdemo.repo.VetRepository;
import com.github.vincemann.springrapid.syncdemo.service.VetService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Primary
@Service
@EnableAutoBiDir
@LogInteraction
public class JpaVetService
        extends JpaCrudService<Vet,Long, VetRepository>
                implements VetService {

    @Transactional
    @Override
    public Optional<Vet> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }
}
