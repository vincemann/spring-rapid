package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.test.util.AopTestUtils;

import java.lang.reflect.Proxy;

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

    // just use AopProxyUtils.getTargetClass -> CrudService implements TargetClassAware which is checked by this method
//    /**
//     * supports aop glibc proxies and libs jdk proxies -> {@link ExtensionProxy}.
//
//     */
//    public static Class<?> getExtensionProxyTargetClass(Object proxy){
//        if (isJDKProxy(proxy) && proxy instanceof CrudService){
//            ExtensionProxy extensionProxy = getExtensionProxy(((CrudService<?, ?>) proxy));
//            return extensionProxy.getProxied().getClass();
//        }
//        throw new IllegalArgumentException("not a spring rapid extension proxy");
//    }

    public static <S> ExtensionProxy getExtensionProxy(S service){
        return (ExtensionProxy) Proxy.getInvocationHandler(AopTestUtils.getUltimateTargetObject(service));
    }

    public static boolean isJDKProxy(Object obj) {
        return Proxy.isProxyClass(obj.getClass()) &&
                Proxy.getInvocationHandler(obj) != null;
    }
    public static boolean isRootService(Object target) {
        if (target instanceof ServiceExtension)
            return false;
        return !Proxy.isProxyClass(target.getClass()) || Proxy.getInvocationHandler(target) == null;
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

    public static boolean isHibernateProxy(Object maybeProxy) throws ClassCastException {
        return maybeProxy instanceof HibernateProxy;

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

//    public static boolean isRootService(Object target) {
////        Class<?> userClass = ProxyUtils.getUserClass(target.getTarget());
//        if (AopUtils.isAopProxy(target) || AopUtils.isCglibProxy(target) || Proxy.isProxyClass(target.getClass()) || target instanceof AbstractServiceExtension) {
//            return false;
//        } else {
//            return true;
//        }
//    }


}
