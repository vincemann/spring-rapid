package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.repo.VetRepository;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.service.JpaUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;




@Root
@Service
public class JpaVetService
        extends JpaUserService<Vet,Long, VetRepository>
                implements VetService {

    @LogInteraction
    @Transactional
    @Override
    public Optional<Vet> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }
}
