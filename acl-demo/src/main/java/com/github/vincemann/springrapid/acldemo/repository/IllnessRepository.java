package com.github.vincemann.springrapid.acldemo.repository;

import com.github.vincemann.springrapid.acldemo.model.Illness;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IllnessRepository extends JpaRepository<Illness,Long> {
    Optional<Illness> findByName(String name);
}
