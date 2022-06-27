package com.github.vincemann.logutil.repo;

import com.github.vincemann.logutil.model.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEntityRepository extends JpaRepository<LogEntity,Long> {
}
