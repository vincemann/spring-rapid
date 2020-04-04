package io.github.vincemann.springrapid.demo.repositories;

import io.github.vincemann.springrapid.demo.model.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VetRepository extends JpaRepository<Vet,Long> {
}
