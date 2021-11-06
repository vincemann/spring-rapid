package com.github.vincemann.springrapid.coredemo.repo;

import com.github.vincemann.springrapid.coredemo.model.LazyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface LazyItemRepository extends JpaRepository<LazyItem,Long> {
}
