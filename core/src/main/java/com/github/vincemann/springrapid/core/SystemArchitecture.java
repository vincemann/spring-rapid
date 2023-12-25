package com.github.vincemann.springrapid.core;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SystemArchitecture {

//    @Pointcut("within(*.service.*)")
//    public void inServiceLayer(){}


    @Pointcut("execution(* save(..))")
    public void saveOperation(){}

    /**
     * Also save bc on repo level update = save with set id
     * Impl should check if id is set
     */
    @Pointcut("execution(* fullUpdate(..))")
    public void fullUpdateOperation(){}

    @Pointcut("execution(* partialUpdate(..))")
    public void partialUpdateOperation(){}

    @Pointcut("execution(* softUpdate(..))")
    public void softUpdateOperation(){}

    @Pointcut("execution(* deleteById(..))")
    public void deleteOperation(){}


    @Pointcut("!execution(* getEntityClass(..))" +
            " && !execution(* getTargetClass(..))" +
            " && !execution(* getBeanName(..))" +
            " && !execution(* inject*(..))"
    )
    public void ignoreHelperServiceMethods(){}

    @Pointcut("execution(!com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension+ *(..)) && !this(com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension+)")
    public void ignoreExtensions(){}

    @Pointcut("execution(public * com.github.vincemann.springrapid.core.service.CrudService+.*(..))")
    public void serviceOperation(){}


    @Pointcut("execution(public * org.springframework.data.repository.Repository+.*(..))")
    public void repoOperation(){}


}
