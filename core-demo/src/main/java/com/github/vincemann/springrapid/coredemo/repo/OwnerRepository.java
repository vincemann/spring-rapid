package com.github.vincemann.springrapid.coredemo.repo;


import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface OwnerRepository extends RapidJpaRepository<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
}
