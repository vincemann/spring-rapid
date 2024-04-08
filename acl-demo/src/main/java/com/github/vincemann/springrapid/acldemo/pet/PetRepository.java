package com.github.vincemann.springrapid.acldemo.pet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet,Long> {
    public Optional<Pet> findByName(String name);
}
