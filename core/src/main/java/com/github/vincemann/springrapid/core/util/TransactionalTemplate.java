package com.github.vincemann.springrapid.core.util;

import lombok.Getter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Getter
public class TransactionalTemplate {


    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void doInTransaction(Runnable runnable) {
        runnable.run();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doInNewTransaction(Runnable runnable) {
        runnable.run();
    }
}
