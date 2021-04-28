package com.github.vincemann.springrapid.acldemo.repositories;

import com.github.vincemann.springrapid.acldemo.model.Toy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ToyRepository extends JpaRepository<Toy,Long> {
    Optional<Toy> findByName(String name);
}
