package com.github.vincemann.springrapid.syncdemo.repo;


import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
@Component
public interface PetRepository extends RapidJpaRepository<Pet,Long> {
    public Optional<Pet> findByName(String name);
    public Set<Pet> findAllByOwnerId(Long ownerId);
}
