package com.github.vincemann.springrapid.coredemo.repo;


import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface SpecialtyRepository extends RapidJpaRepository<Specialty,Long> {
    Optional<Specialty> findByDescription(String description);
}
