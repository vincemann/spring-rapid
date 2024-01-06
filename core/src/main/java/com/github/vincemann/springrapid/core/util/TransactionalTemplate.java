package com.github.vincemann.springrapid.core.util;

import lombok.Getter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Getter
public class TransactionalTemplate {


    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(/*isolation = READ_COMMITTED*/)
    public void doInTransaction(Runnable runnable) {
        runnable.run();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doInNewTransaction(Runnable runnable) {
        runnable.run();
    }
}
