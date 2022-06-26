package com.github.vincemann.springrapid.coredemo.log;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleLogChildRepository extends JpaRepository<SingleLogChild,Long> {
}
