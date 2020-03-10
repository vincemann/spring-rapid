package io.github.vincemann.generic.crud.lib.service.sessionReattach;

//
//import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
//import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.Hibernate;
//import org.hibernate.proxy.HibernateProxy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//import java.util.Set;
//
//@Slf4j
//public class EntityGraphSessionReattacher {
//
//    @Getter
//    private SessionReattacher sessionReattacher;
//
//    public EntityGraphSessionReattacher(SessionReattacher sessionReattacher) {
//        this.sessionReattacher = sessionReattacher;
//    }
//
//    /**
//     * Recursively searches for Objects of Type {@link javax.persistence.Entity}
//     * starting from Root Entity.
//     * It is expected, that the Fields extend {@link io.github.vincemann.generic.crud.lib.model.IdentifiableEntity}
//     *
//     * Root node itself is NOT attached to current session
//     */
//    public void attachEntityGraphToCurrentSession(Object root){
//        try {
//            //todo are hibernate proxys a problem here? If so, see HibernateForceEagerFetchUtil recursive solution
//            Set<Object> identifiableEntitiesInEntityGraph = ReflectionUtils.findObjects_OfAllMemberVars_AssignableFrom(root, IdentifiableEntity.class, true);
//            identifiableEntitiesInEntityGraph.remove(root);
//            for (Object entity : identifiableEntitiesInEntityGraph) {
//                Serializable id = ((IdentifiableEntity) entity).getId();
//                if(id!=null){
//                    boolean success = sessionReattacher.attachToCurrentSession(entity);
//                    if(!success){
//                        log.warn("entity was recognized as unattached to curr session, but was attached: " + entity);
//                    }
//                }
//            }
//        }catch (IllegalAccessException e){
//            throw new RuntimeException(e);
//        }
//    }
//
//}
