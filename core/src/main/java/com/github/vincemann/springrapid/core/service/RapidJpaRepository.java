package com.github.vincemann.springrapid.core.service;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RapidJpaRepository<T, ID> extends JpaRepository<T, ID> {

    public T update(T entity, Boolean partial);
}
