package com.github.vincemann.springrapid.acldemo.repositories;

import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.core.service.RapidJpaRepository;

import java.util.Optional;

public interface IllnessRepository extends RapidJpaRepository<Illness,Long> {
    Optional<Illness> findByName(String name);
}
