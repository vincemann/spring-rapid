package com.github.vincemann.springrapid.coretest.controller;

import com.github.vincemann.springrapid.coretest.slicing.TestComponent;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@TestComponent
public class TransactionalTestTemplate {

    @Transactional
    public void doInTransaction(Runnable runnable) {
        runnable.run();
    }
}
