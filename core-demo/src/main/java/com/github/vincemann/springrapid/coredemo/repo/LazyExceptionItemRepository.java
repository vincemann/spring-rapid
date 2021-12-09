package com.github.vincemann.springrapid.coredemo.repo;

import com.github.vincemann.springrapid.coredemo.model.LazyExceptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LazyExceptionItemRepository extends JpaRepository<LazyExceptionItem,Long> {
}
