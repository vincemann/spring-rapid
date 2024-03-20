package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.coredemo.service.Root;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.repo.PetRepository;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Root
@Service
@EnableAutoBiDir
public class JpaPetService
        extends AbstractCrudService<Pet, Long, PetRepository>
                implements PetService {

    @Transactional
    @Override
    public Optional<Pet> findByName(String petName) {
        return getRepository().findByName(petName);
    }
}
