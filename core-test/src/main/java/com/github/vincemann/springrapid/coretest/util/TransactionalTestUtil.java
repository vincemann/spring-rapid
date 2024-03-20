package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;

public class TransactionalTestUtil {

    public static void withinTransaction(TransactionTemplate transactionTemplate, ThrowingRunnable runnable) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void clear(JpaRepository<? extends IdAwareEntity<Serializable>, Serializable> jpaRepository, TransactionTemplate transactionTemplate) {
        transactionTemplate.executeWithoutResult(status -> {
            for (IdAwareEntity<Serializable> entity : jpaRepository.findAll()) {
                System.err.println("removing entity: " + entity);
                jpaRepository.deleteById(entity.getId());
            }
        });
    }


}
