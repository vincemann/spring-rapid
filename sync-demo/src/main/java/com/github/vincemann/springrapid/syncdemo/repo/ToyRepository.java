package com.github.vincemann.springrapid.syncdemo.repo;

import com.github.vincemann.springrapid.syncdemo.model.Toy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToyRepository extends JpaRepository<Toy,Long> {
    Optional<Toy> findByName(String name);
}
