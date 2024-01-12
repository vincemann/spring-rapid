package com.github.vincemann.springrapid.syncdemo.repo;

import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;

//@DisableAutoBiDir
public interface ClinicCardRepository extends RapidJpaRepository<ClinicCard,Long> {
}
