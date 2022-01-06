package com.github.vincemann.springrapid.coredemo.repo;

import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Toy;
import com.github.vincemann.springrapid.core.service.RapidJpaRepository;

import java.util.Optional;

public interface ToyRepository extends RapidJpaRepository<Toy,Long> {
    Optional<Toy> findByName(String name);
}
