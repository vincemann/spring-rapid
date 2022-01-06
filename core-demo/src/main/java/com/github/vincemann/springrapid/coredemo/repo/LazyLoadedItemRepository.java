package com.github.vincemann.springrapid.coredemo.repo;

import com.github.vincemann.springrapid.core.service.RapidJpaRepository;
import com.github.vincemann.springrapid.coredemo.model.LazyLoadedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LazyLoadedItemRepository extends RapidJpaRepository<LazyLoadedItem,Long> {
}
