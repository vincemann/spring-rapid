package io.github.vincemann.generic.crud.lib.util;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class SpringProxyUtils {

    @SuppressWarnings({"unchecked"})
    public static  <T> T getTargetObject(Object proxy){
        try {
            while( (AopUtils.isJdkDynamicProxy(proxy))) {
                return (T) getTargetObject(((Advised)proxy).getTargetSource().getTarget());
            }
            return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
        }catch (Exception e){
            throw new RuntimeException("Could not unproxy",e);
        }

    }

}
