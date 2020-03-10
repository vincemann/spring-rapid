package io.github.vincemann.generic.crud.lib.service.sessionReattach;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
//
//@Primary
//@Slf4j
//public class EntityMangerSessionReattacher implements SessionReattacher {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//
//    /**
//     *
//     * @param entity
//     * @return  false if entity was already attached to session
//     */
//    public boolean attachToCurrentSession(Object entity) {
//        Session session = entityManager.unwrap(Session.class);
//        if (session.contains(entity)) {
//            // nothing to do... entity is already associated with the session
//            return false;
//        } else {
//            //attach
//            log.debug("reattaching: " + entity+ " to session: " + session);
//            session.saveOrUpdate(entity);
//            return true;
//        }
//    }
//}
