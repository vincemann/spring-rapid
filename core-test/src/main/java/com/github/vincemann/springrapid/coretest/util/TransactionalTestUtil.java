package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;

public class TransactionalTestUtil {


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
