package com.github.vincemann.springrapid.core;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SystemArchitecture {

    @Pointcut("execution(* save(..))")
    public void saveOperation(){}

    @Pointcut("execution(* fullUpdate(..))")
    public void fullUpdateOperation(){}

    @Pointcut("execution(* partialUpdate(..))")
    public void partialUpdateOperation(){}

    @Pointcut("execution(* softUpdate(..))")
    public void softUpdateOperation(){}

    @Pointcut("execution(* deleteById(..))")
    public void deleteOperation(){}

    @Pointcut("execution(* findById(..))")
    public void findByIdOperation(){}

    @Pointcut("!execution(* getEntityClass(..))" +
            " && !execution(* getTargetClass(..))" +
            " && !execution(* toString(..))" +
            " && !execution(* getBeanName(..))" +
            " && !execution(* inject*(..))"
    )
    public void ignoreHelperServiceMethods(){}

    @Pointcut("!this(com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension+)")
    public void ignoreExtensions(){}

    @Pointcut("!target(java.lang.reflect.Proxy)")
    public void ignoreJdkProxies(){}

    @Pointcut("this(com.github.vincemann.springrapid.core.service.CrudService+)")
    public void serviceOperation(){}


    @Pointcut("execution(public * org.springframework.data.repository.Repository+.*(..))")
    public void repoOperation(){}


}
