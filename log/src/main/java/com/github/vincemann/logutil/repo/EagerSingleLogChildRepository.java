package com.github.vincemann.logutil.repo;

import com.github.vincemann.logutil.model.EagerSingleLogChild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.stereotype.Repository;

@Repository
public interface EagerSingleLogChildRepository extends JpaRepository<EagerSingleLogChild,Long> {
}
