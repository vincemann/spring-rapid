package com.github.vincemann.springrapid.syncdemo.repo;

import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.syncdemo.model.Vet;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ServiceComponent
public interface VetRepository extends RapidJpaRepository<Vet,Long> {
    Optional<Vet> findByLastName(String lastName);
}
