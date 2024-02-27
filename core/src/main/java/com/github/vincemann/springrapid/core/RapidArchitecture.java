package com.github.vincemann.springrapid.core;

import org.aspectj.lang.annotation.Pointcut;

public class RapidArchitecture {

    @Pointcut("execution(* create(..))")
    public void createOperation(){}

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


    @Pointcut("this(com.github.vincemann.springrapid.core.service.CrudService+)")
    public void serviceOperation(){}


    @Pointcut("execution(public * org.springframework.data.repository.Repository+.*(..))")
    public void repoOperation(){}


}
