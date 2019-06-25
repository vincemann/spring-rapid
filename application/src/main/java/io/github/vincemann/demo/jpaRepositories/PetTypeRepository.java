package io.github.vincemann.demo.jpaRepositories;

import io.github.vincemann.demo.model.PetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetTypeRepository extends JpaRepository<PetType,Long> {
}
