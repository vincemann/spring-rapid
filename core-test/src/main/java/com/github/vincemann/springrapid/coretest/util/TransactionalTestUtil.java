package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.assertj.core.api.AbstractSoftAssertions;
import org.assertj.core.api.ThrowableAssert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.function.Consumer;

public class TransactionalTestUtil {

    public static void withinTransaction(TransactionTemplate transactionTemplate, ThrowingRunnable runnable){
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void clear(CrudService crudService, TransactionTemplate transactionTemplate) {
        transactionTemplate.executeWithoutResult(status -> {
            for (IdentifiableEntity entity : (Collection<IdentifiableEntity>) crudService.findAll()) {
                System.err.println("removing entity: " + entity);
                try {
                    crudService.deleteById(entity.getId());
                } catch (EntityNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void clear(JpaRepository jpaRepository, TransactionTemplate transactionTemplate) {
        transactionTemplate.executeWithoutResult(status -> {
            for (IdentifiableEntity entity : (Collection<IdentifiableEntity>) jpaRepository.findAll()) {
                System.err.println("removing entity: " + entity);
                jpaRepository.deleteById(entity.getId());
            }
        });
    }


}
