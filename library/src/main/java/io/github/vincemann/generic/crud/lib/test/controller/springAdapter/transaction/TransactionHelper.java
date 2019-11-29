package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.transaction;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class TransactionHelper {

    private EntityManager entityManager;

    @Autowired
    public TransactionHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     *
     * @param entity
     * @return  false if entity was already attached to session
     */
    public boolean attachToCurrentSession(Object entity) {
        Session session = entityManager.unwrap(Session.class);
        if (session.contains(entity)) {
            // nothing to do... entity is already associated with the session
            return false;
        } else {
            //attach
            session.saveOrUpdate(entity);
            return true;
        }
    }
}
