package com.github.vincemann.logutil.repo;

import com.github.vincemann.logutil.model.LogChild2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogChild2Repository extends JpaRepository<LogChild2,Long> {
}
