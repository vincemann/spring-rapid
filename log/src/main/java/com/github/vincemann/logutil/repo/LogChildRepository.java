package com.github.vincemann.logutil.repo;

import com.github.vincemann.logutil.model.LogChild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogChildRepository extends JpaRepository<LogChild,Long> {
}
