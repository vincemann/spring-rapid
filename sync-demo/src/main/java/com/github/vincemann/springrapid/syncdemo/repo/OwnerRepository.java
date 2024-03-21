package com.github.vincemann.springrapid.syncdemo.repo;


import com.github.vincemann.springrapid.syncdemo.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
}
