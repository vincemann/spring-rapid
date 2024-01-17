package com.github.vincemann.springrapid.syncdemo.repo;


import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
@ServiceComponent
public interface PetRepository extends RapidJpaRepository<Pet,Long> {
    public Optional<Pet> findByName(String name);
    public Set<Pet> findAllByOwnerId(Long ownerId);
}
