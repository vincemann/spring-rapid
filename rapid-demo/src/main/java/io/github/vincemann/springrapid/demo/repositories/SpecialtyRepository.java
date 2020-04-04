package io.github.vincemann.springrapid.demo.repositories;

import io.github.vincemann.springrapid.demo.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty,Long> {
}
