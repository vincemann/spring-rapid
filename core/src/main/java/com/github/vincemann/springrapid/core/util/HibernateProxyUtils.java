package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

public abstract class HibernateProxyUtils {

    private HibernateProxyUtils(){}

    /**
     * gets Class of hibernate proxy without initializing entity ( = loading all lazy entities )
     */
    public static <T> Class<T> getTargetClass(T proxied) {
        T entity = proxied;
        if (entity instanceof HibernateProxy) {
            HibernateProxy hibernateProxy = (HibernateProxy) entity;
            return (Class<T>) hibernateProxy.getHibernateLazyInitializer().getPersistentClass();
        } else {
            return (Class<T>) proxied.getClass();
        }
    }

    public static boolean jpaEquals(IdAwareEntity entity, IdAwareEntity other) {
        if (entity == other) return true;
        if (entity == null || other == null ||
                HibernateProxyUtils.getTargetClass(entity) != HibernateProxyUtils.getTargetClass(other)) {
            return false;
        }

        // todo maybe access the id via reflections, so proxy is not initialized -> is against jpa spec tho
        Object entityId = entity.getId();
        Object otherId = other.getId();

        if (entityId == null || otherId == null) {
            return false; // If either ID is null, they are not equal
        }

        return entityId.equals(otherId);
    }

    public static <T> T unproxy(T proxied)
    {
        T entity = proxied;
        if (entity instanceof HibernateProxy) {
            // why would I want to load all properties?
            Hibernate.initialize(entity);
            entity = (T) ((HibernateProxy) entity)
                    .getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }

    public static boolean unproxyEquals(Object o1, Object o2){
        return Hibernate.unproxy(o1).equals(Hibernate.unproxy(o2));
    }

    public static <T> T hibernateUnproxyRaw(T proxied){
        return (T) Hibernate.unproxy(proxied);
    }


}
