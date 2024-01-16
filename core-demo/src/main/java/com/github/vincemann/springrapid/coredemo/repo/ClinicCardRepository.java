package com.github.vincemann.springrapid.coredemo.repo;


import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import org.springframework.data.jpa.repository.JpaRepository;

//@DisableAutoBiDir
public interface ClinicCardRepository extends JpaRepository<ClinicCard,Long> {
}
