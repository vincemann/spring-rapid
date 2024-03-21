package com.github.vincemann.springrapid.syncdemo.repo;


import com.github.vincemann.springrapid.syncdemo.model.PetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetTypeRepository extends JpaRepository<PetType,Long> {
}
