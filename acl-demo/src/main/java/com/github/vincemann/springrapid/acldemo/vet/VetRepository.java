package com.github.vincemann.springrapid.acldemo.vet;

import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VetRepository extends AbstractUserRepository<Vet,Long> {
    Optional<Vet> findByLastName(String lastName);
}
