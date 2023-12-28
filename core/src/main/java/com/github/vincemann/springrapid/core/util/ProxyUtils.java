package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.hibernate.Hibernate;
import org.hibernate.engine.spi.SessionImplementor;
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

    public static <S extends CrudService<?,?>> ServiceExtensionProxy<S> getExtensionProxy(S service){
        return (ServiceExtensionProxy<S>) Proxy.getInvocationHandler(AopTestUtils.getUltimateTargetObject(service));
    }

    public static boolean isJDKProxy(Object obj) {
        return Proxy.isProxyClass(obj.getClass()) &&
                Proxy.getInvocationHandler(obj) instanceof java.lang.reflect.InvocationHandler;
    }
    public static boolean isRootService(Object target) {
        if (target instanceof AbstractServiceExtension)
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

    public static <T> Class<T> getTargetClass(Object proxied){
        return (Class<T>) hibernateUnproxy(proxied).getClass();
    }

    public static <T> T hibernateUnproxy(T proxied)
    {
        T entity = proxied;
        if (entity instanceof HibernateProxy) {
            Hibernate.initialize(entity);
            entity = (T) ((HibernateProxy) entity)
                    .getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
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
