package com.github.vincemann.springrapid.syncdemo.repo;


import com.github.vincemann.springrapid.syncdemo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PetRepository extends JpaRepository<Pet,Long> {
    public Optional<Pet> findByName(String name);
    public Set<Pet> findAllByOwnerId(Long ownerId);
}
