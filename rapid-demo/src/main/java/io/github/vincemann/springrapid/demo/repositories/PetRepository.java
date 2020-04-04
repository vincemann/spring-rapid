package io.github.vincemann.springrapid.demo.repositories;

import io.github.vincemann.springrapid.demo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet,Long> {
}
