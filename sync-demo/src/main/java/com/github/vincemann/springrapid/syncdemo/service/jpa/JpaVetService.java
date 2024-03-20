package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
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
public class JpaVetService
        extends AbstractCrudService<Vet,Long, VetRepository>
                implements VetService {

    @Transactional
    @Override
    public Optional<Vet> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }
}
