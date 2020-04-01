package io.github.vincemann.generic.crud.lib.advice.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LogComponentInteractionAdvice {

    @Around("@annotation(LogInteraction)")
    public Object prePersistBiDirParent(ProceedingJoinPoint joinPoint) throws Throwable {
        String padding = "###################################";
        String method = joinPoint.getTarget().getClass().getSimpleName() + "-" + joinPoint.getSignature().getName();
        log.debug("");
        log.debug(padding+"  INPUT for " + method +"  "+padding);
        Arrays.stream(joinPoint.getArgs()).forEach(arg -> log.debug(arg.toString()));
        log.debug("");
        Object ret = joinPoint.proceed();
        log.debug("");
        log.debug(padding+"  OUTPUT of: "+method+"  "+padding);
        if(ret==null){
            log.debug("null");
        }else {
            log.debug(ret.toString());
        }
        log.debug("");
        return ret;
    }

}
