package com.github.vincemann.springrapid.core.service;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

public class RapidJpaRepositoryImpl<T, ID>
        extends SimpleJpaRepository<T, ID>
        implements RapidJpaRepository<T, ID> {

    public RapidJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public RapidJpaRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    public T update(T entity) {
        return save(entity);
    }
}
