package com.github.vincemann.springrapid.acldemo.repo;

import com.github.vincemann.springrapid.acldemo.model.Vet;

import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface VetRepository extends RapidJpaRepository<Vet,Long> {
    Optional<Vet> findByLastName(String lastName);
}
