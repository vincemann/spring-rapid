package com.github.vincemann.springrapid.coredemo.repo;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.core.service.RapidJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ServiceComponent
public interface VetRepository extends RapidJpaRepository<Vet,Long> {
    Optional<Vet> findByLastName(String lastName);
}
