package com.github.vincemann.springrapid.coredemo.repo;


import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface PetTypeRepository extends RapidJpaRepository<PetType,Long> {
}
