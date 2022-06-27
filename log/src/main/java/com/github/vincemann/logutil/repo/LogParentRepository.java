package com.github.vincemann.logutil.repo;

import com.github.vincemann.logutil.model.LogParent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogParentRepository extends JpaRepository<LogParent,Long> {
}
