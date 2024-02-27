package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.test.util.AopTestUtils;

public class ProxyUtils {


    //use whenever you get errors comparing cglib proxy (all fields null) with normal object or other proxy
    public static boolean isEqual(Object o1, Object o2){
        //other way around is no problem
        //dont forget to actually implement the equals method with getters !
        if (AopUtils.isCglibProxy(o1)){
            return AopTestUtils.getUltimateTargetObject(o1).equals(o2);
        }else {
            return o1.equals(o2);
        }
    }



    /**
     * Use in combination with @{@link org.springframework.boot.test.mock.mockito.SpyBean}.
     * If you get something like : rg.mockito.exceptions.misusing.NotAMockException: Argument should be a mock, but is: class com.blah.MyServiceImpl$$EnhancerBySpringCGLIB$$9712a2a5
     * example:
     *
     *
     * @SpyBean
     * Extension extension;
     *
     * ...
     *
     * doReturn(Boolean.TRUE)
     *                 .when(unproxy(extension)).isInTimeFrame(any(Rating.class));
     */
    public static  <T> T aopUnproxy(T proxy){
        //        https://stackoverflow.com/questions/9033874/mocking-a-property-of-a-cglib-proxied-service-not-working
        return AopTestUtils.getUltimateTargetObject(proxy);
    }

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

    public static boolean jpaEquals(IdentifiableEntity entity, IdentifiableEntity other) {
        if (entity == other) return true;
        if (entity == null || other == null ||
                ProxyUtils.getTargetClass(entity) != ProxyUtils.getTargetClass(other)) {
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

    public static <T> T hibernateUnproxy(T proxied)
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
