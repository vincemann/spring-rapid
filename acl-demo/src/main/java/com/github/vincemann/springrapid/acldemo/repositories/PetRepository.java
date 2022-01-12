package com.github.vincemann.springrapid.acldemo.repositories;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ServiceComponent
public interface PetRepository extends JpaRepository<Pet,Long> {
    public Optional<Pet> findByName(String name);
}
