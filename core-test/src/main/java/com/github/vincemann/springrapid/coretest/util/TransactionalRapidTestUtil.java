package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public class TransactionalRapidTestUtil {



    public static <E extends IdentifiableEntity> E mustBePresentIn(CrudRepository repo, Serializable id) {
        Optional byId = repo.findById(id);
        if (byId.isEmpty()) {
            throw new IllegalArgumentException("No Entity found with id: " + id);
        }
        return (E) byId.get();
    }


    public static <E extends IdentifiableEntity> E mustBePresentIn(CrudService service, Serializable id) {
        Optional byId;
        byId = service.findById(id);
        if (byId.isEmpty()) {
            throw new IllegalArgumentException("No Entity found with id: " + id);
        }
        return (E) byId.get();
    }

    public static void clear(CrudService crudService, TransactionTemplate transactionTemplate) {
        transactionTemplate.execute(status -> {
            for (IdentifiableEntity entity : (Collection<IdentifiableEntity>) crudService.findAll()) {
                System.err.println("removing entity: " + entity);
                try {
                    crudService.deleteById(entity.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        });
    }

    public static void clear(JpaRepository jpaRepository, TransactionTemplate transactionTemplate) {
        transactionTemplate.execute(status -> {
            for (IdentifiableEntity entity : (Collection<IdentifiableEntity>) jpaRepository.findAll()) {
                System.err.println("removing entity: " + entity);
                try {
                    jpaRepository.deleteById(entity.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        });
    }


}
