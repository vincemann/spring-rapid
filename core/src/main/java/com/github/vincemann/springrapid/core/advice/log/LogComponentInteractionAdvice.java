package com.github.vincemann.springrapid.core.advice.log;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Slf4j
public class LogComponentInteractionAdvice {
    public static String HARD_START_PADDING_CHAR = "+";
    public static String HARD_END_PADDING_CHAR = "=";
    public static String SOFT_PADDING_CHAR = "_";
    public static int LENGTH = 122;
    public static int INDENTATION_LENGTH = 16;
    public static String INDENTATION_CHAR = " ";


    public static void logCall(String methodName, String clazz,Object... args){
        logCall(methodName,clazz,LogInteraction.Level.DEBUG,0,args);
    }

    public static void logCall(String methodName, String clazz, LogInteraction.Level level,int indentBy, Object...args){
        Logger logger = new Logger(level);
        String middlePart = "  INPUT for " + clazz + "-" + methodName + "  ";
        String startPadding = createPadding(middlePart, HARD_START_PADDING_CHAR);
        String indentation = INDENTATION_CHAR.repeat(indentBy*INDENTATION_LENGTH);
        logger.log("");
        logger.log(indentation+startPadding +middlePart+ startPadding);
        AtomicInteger index = new AtomicInteger(1);
        Arrays.stream(args).forEach(arg -> logger.log(index.getAndIncrement()+": " +arg.toString()));
        String softPadding = createPadding("", SOFT_PADDING_CHAR);
        logger.log(indentation+softPadding+softPadding);
        logger.log("");
    }

    public static void logResult(String method,String clazz, LogInteraction.Level level, int indentBy, Object ret){
        Logger logger = new Logger(level);
        logger.log("");
        String middlePart = "  OUTPUT of: "+clazz+ "-" + method + "  ";
        String padding = createPadding(middlePart, SOFT_PADDING_CHAR);
        String indentation = INDENTATION_CHAR.repeat(indentBy*INDENTATION_LENGTH);
        logger.log(indentation+padding+middlePart+padding);
        if(ret==null){
            logger.log(indentation+"-> null");
        }else {
            logger.log(indentation+"-> " +ret.toString());
        }
        String endPadding = createPadding("", HARD_END_PADDING_CHAR);
        logger.log(indentation+endPadding+endPadding);
        logger.log("");
    }
    public static void logResult(String method,String clazz, Object ret){
        logResult(method,clazz,LogInteraction.Level.DEBUG,0,ret);
    }

    private static String createPadding(String middlePart,String paddingChar){
        int paddingLength = (LENGTH-middlePart.length())/2;
        return paddingChar.repeat(paddingLength);
    }

    @Around("@annotation(com.github.vincemann.springrapid.core.advice.log.LogInteraction)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        LogInteraction logInteraction = extractAnnotation(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        String clazzName = joinPoint.getTarget().getClass().getSimpleName();
        Object ret;
        if (logInteraction==null){
            log.warn("Could not find LogInteraction annotation of method: " + methodName);
            logCall(methodName,
                    clazzName,
                    joinPoint.getArgs()
            );
            ret = joinPoint.proceed();
            logResult(
                    methodName,
                    clazzName,
                    ret
            );
        }else {
            if (logInteraction.args()) {
                logCall(methodName,
                        clazzName,
                        logInteraction,
                        joinPoint.getArgs()
                );
            }
            ret = joinPoint.proceed();
            if (logInteraction.result()) {
                logResult(
                        methodName,
                        clazzName,
                        logInteraction.level(),
                        logInteraction.indentBy(),
                        ret
                );
            }
        }
        return ret;
    }
    
    @AllArgsConstructor
    private static class Logger{
        LogInteraction.Level level;
        private void log(String s){
            switch (level){
                case INFO:
                    log.info(s);
                    break;
                case WARN:
                    log.warn(s);
                    break;
                case DEBUG:
                    log.debug(s);
                    break;
                case ERROR:
                    log.error(s);
                    break;
            }
        }
    }
    
    
    private static LogInteraction extractAnnotation(ProceedingJoinPoint joinPoint){
        int paramCount = joinPoint.getArgs().length;
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(joinPoint.getTarget().getClass(), LogInteraction.class);
        Optional<Method> method = methods.stream().filter(m -> m.getName().equals(joinPoint.getSignature().getName())
                && m.getParameterCount() == paramCount
        ).findFirst();
        return method
                .map(value -> value.getAnnotation(LogInteraction.class))
                .orElse(null);
    }

}
