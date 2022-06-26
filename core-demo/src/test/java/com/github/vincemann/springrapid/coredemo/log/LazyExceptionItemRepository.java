package com.github.vincemann.springrapid.coredemo.log;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LazyExceptionItemRepository extends JpaRepository<LogParent,Long> {
}
