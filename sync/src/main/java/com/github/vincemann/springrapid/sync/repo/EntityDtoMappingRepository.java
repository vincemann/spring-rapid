package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.sync.model.EntityDtoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityDtoMappingRepository extends JpaRepository<EntityDtoMapping, Long> {
    // Methods to find/update DTO mappings by entity class, entity ID, and DTO class
}

