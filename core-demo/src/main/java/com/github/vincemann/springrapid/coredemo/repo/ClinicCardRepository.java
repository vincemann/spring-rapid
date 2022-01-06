package com.github.vincemann.springrapid.coredemo.repo;

import com.github.vincemann.springrapid.core.service.RapidJpaRepository;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicCardRepository extends RapidJpaRepository<ClinicCard,Long> {
}
