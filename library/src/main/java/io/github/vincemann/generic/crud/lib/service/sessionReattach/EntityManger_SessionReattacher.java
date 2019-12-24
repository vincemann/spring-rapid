package io.github.vincemann.generic.crud.lib.service.sessionReattach;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Component
@Primary
public class EntityManger_SessionReattacher implements SessionReattacher {

    private EntityManager entityManager;

    @Autowired
    public EntityManger_SessionReattacher(EntityManager entityManager) {
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
