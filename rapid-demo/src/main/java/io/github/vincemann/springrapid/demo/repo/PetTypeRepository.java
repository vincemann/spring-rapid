package io.github.vincemann.springrapid.demo.repo;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.demo.model.PetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ServiceComponent
public interface PetTypeRepository extends JpaRepository<PetType,Long> {
}
