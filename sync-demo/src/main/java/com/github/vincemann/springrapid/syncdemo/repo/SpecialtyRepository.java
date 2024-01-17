package com.github.vincemann.springrapid.syncdemo.repo;


import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.syncdemo.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ServiceComponent
public interface SpecialtyRepository extends Specialty,Long> {
    Optional<Specialty> findByDescription(String description);
}
