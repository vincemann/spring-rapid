package com.github.vincemann.springrapid.acldemo.repositories;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ServiceComponent
public interface VetRepository extends JpaRepository<Vet,Long> {
    Optional<Vet> findByLastName(String lastName);
}
