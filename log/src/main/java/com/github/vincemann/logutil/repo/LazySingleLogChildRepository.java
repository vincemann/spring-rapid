package com.github.vincemann.logutil.repo;

import com.github.vincemann.logutil.model.LazySingleLogChild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LazySingleLogChildRepository extends JpaRepository<LazySingleLogChild,Long> {
}
