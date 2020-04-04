package io.github.vincemann.springrapid.core.advice.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
@Slf4j
public class LogComponentInteractionAdvice {
    private static final String HARD_PADDING = "#";
    private static final String SOFT_PADDING = "_";
    private static final int LENGTH = 122;

    public static void logArgs(String method,Object... args){
        logArgs(method,"",args);
    }

    private static void logArgs(String method, String clazz, Object...args){
        String middlePart = "  INPUT for " + clazz + "-" + method + "  ";
        String padding = createPadding(middlePart, HARD_PADDING);
        log.debug("");
        log.debug(padding +middlePart+ padding);
        AtomicInteger index = new AtomicInteger(1);
        Arrays.stream(args).forEach(arg -> log.debug(index.getAndIncrement()+": " +arg.toString()));
        String softPadding = createPadding("", SOFT_PADDING);
        log.debug(softPadding+softPadding);
        log.debug("");
    }

    public static void logResult(String method,Object ret){
        log.debug("");
        String middlePart = "  OUTPUT of: " + method + "  ";
        String padding = createPadding(middlePart,SOFT_PADDING);
        log.debug(padding+middlePart+padding);
        if(ret==null){
            log.debug("-> null");
        }else {
            log.debug("-> " +ret.toString());
        }
        String hardPadding = createPadding("", HARD_PADDING);
        log.debug(hardPadding+hardPadding);
        log.debug("");
    }

    private static String createPadding(String middlePart,String paddingChar){
        int paddingLength = (LENGTH-middlePart.length())/2;
        return paddingChar.repeat(paddingLength);
    }

    @Around("@annotation(io.github.vincemann.springrapid.core.advice.log.LogInteraction)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        logArgs(joinPoint.getSignature().getName(),joinPoint.getTarget().getClass().getSimpleName(),joinPoint.getArgs());
        Object ret = joinPoint.proceed();
        logResult(joinPoint.getSignature().getName(),ret);
        return ret;
    }

}
