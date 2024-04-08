package com.github.vincemann.springrapid.acldemo.other;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty,Long> {
    Optional<Specialty> findByDescription(String description);
}
