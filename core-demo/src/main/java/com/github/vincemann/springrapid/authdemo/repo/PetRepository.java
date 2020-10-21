package com.github.vincemann.springrapid.authdemo.repo;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.authdemo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ServiceComponent
public interface PetRepository extends JpaRepository<Pet,Long> {
}
