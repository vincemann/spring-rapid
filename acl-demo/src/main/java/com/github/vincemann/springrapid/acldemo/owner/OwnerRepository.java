package com.github.vincemann.springrapid.acldemo.owner;

import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends AbstractUserRepository<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
}
