package com.github.vincemann.springrapid.syncdemo.repo;

import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.syncdemo.model.Visit;
import org.springframework.stereotype.Repository;

@Repository
@ServiceComponent
public interface VisitRepository extends RapidJpaRepository<Visit,Long> {
}
