package io.github.vincemann.generic.crud.lib.advice;

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

    @Pointcut("execution(* delete(..)) || execution(* deleteById(..))")
    public void deleteOperation(){}
}
