package com.github.vincemann.springrapid.core.advice;

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
    @Pointcut("execution(* save(..))")
    public void updateOperation(){}

    @Pointcut("execution(* deleteById(..))")
    public void deleteOperation(){}


    @Pointcut("execution(public * com.github.vincemann.springrapid.core.service.CrudService+.*(..))")
    public void serviceOperation(){}

    @Pointcut("execution(public * org.springframework.data.repository.Repository+.*(..))")
    public void repoOperation(){}


}
