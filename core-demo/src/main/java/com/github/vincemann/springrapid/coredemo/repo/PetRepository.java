package com.github.vincemann.springrapid.coredemo.repo;


import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface PetRepository extends RapidJpaRepository<Pet,Long> {
    public Optional<Pet> findByName(String name);
}
