package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.aspectj.lang.JoinPoint;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.test.util.AopTestUtils;

import java.lang.reflect.InvocationTargetException;
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

//    public static boolean isRootService(Object target) {
////        Class<?> userClass = ProxyUtils.getUserClass(target.getTarget());
//        if (AopUtils.isAopProxy(target) || AopUtils.isCglibProxy(target) || Proxy.isProxyClass(target.getClass()) || target instanceof AbstractServiceExtension) {
//            return false;
//        } else {
//            return true;
//        }
//    }


}
