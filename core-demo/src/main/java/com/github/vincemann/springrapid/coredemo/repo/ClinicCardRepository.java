package com.github.vincemann.springrapid.coredemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicCardRepository extends JpaRepository<ClinicCard,Long> {
}
