package io.github.vincemann.generic.crud.lib.service.sessionReattach;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Qualifier("test")
@Slf4j
public class TestEntityManger_SessionReattacher implements SessionReattacher {

    private EntityManager entityManager;
    //todo is this impl even acting any different from the non test version?


    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    /**
     *
     * @param entity
     * @return  false if entity was already attached to session
     */
    @Transactional
    public boolean attachToCurrentSession(Object entity) {
        Session session = entityManager.unwrap(Session.class);
        if (session.contains(entity)) {
            // nothing to do... entity is already associated with the session
            return false;
        } else {
            //attach
            log.debug("reattaching: " + entity+ " to session: " + session);
            session.saveOrUpdate(entity);
            return true;
        }
    }
}
