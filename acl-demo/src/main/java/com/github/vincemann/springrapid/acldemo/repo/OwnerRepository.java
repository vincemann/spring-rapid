package com.github.vincemann.springrapid.acldemo.repo;

import com.github.vincemann.springrapid.acldemo.model.Owner;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ServiceComponent
public interface OwnerRepository extends RapidJpaRepository<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
}
