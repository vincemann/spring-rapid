package com.github.vincemann.springrapid.core.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@NoRepositoryBean
public interface RapidJpaRepository<T, ID> extends JpaRepository<T, ID> {

    public T update(T entity);
}
