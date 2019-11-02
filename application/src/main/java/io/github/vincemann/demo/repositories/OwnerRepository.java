package io.github.vincemann.demo.repositories;

import io.github.vincemann.demo.model.Owner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner,Long> {

    Optional<Owner> findByLastName(String lastName);
}
