package com.github.vincemann.springrapid.core.advice.log;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Aspect
@Slf4j
public class LogComponentInteractionAdvice {
    private static final Map<Thread, Integer> thread_amountOpenMethodCalls = new HashMap<>();
    public static String HARD_START_PADDING_CHAR = "+";
    public static String HARD_END_PADDING_CHAR = "=";
    public static String SOFT_PADDING_CHAR = "_";
    public static int LENGTH = 122;
    public static int INDENTATION_LENGTH = 16;
    public static String INDENTATION_CHAR = " ";

    public static void logCall(String methodName, String clazz, LogInteraction.Level level, Object... args) {
        logCall(methodName, clazz, level, false, args);
    }

    private static void logCall(String methodName, String clazz, LogInteraction.Level level, boolean internal, Object... args) {
        int openMethodCalls = 1;
        if (internal) {
            openMethodCalls = incrementOpenMethodCalls();
        }

        String indentation = INDENTATION_CHAR.repeat((openMethodCalls - 1) * INDENTATION_LENGTH);

        Logger logger = new Logger(level);
        String middlePart = "  INPUT for " + clazz + "-" + methodName + getPaddedThreadInfoSuffix();
        String startPadding = createPadding(middlePart, HARD_START_PADDING_CHAR);


        logger.log("");
        logger.log(indentation + startPadding + middlePart + startPadding);

        for (int i = 0; i < args.length; i++) {
            String argInfo = indentation + i + ": " + args[i];

            logger.log(argInfo + getPaddedThreadInfoSuffix());

        }
        String softPadding = createPadding("", SOFT_PADDING_CHAR);

        logger.log(indentation + softPadding + softPadding);
        logger.log("");
    }

    private static int incrementOpenMethodCalls() {
        int openMethodCalls;
        Integer currentCalls = thread_amountOpenMethodCalls.get(Thread.currentThread());
        if (currentCalls == null) {
            thread_amountOpenMethodCalls.put(Thread.currentThread(), 1);
        } else {
            int incremented = currentCalls + 1;
            thread_amountOpenMethodCalls.put(Thread.currentThread(), incremented);
        }
        openMethodCalls = thread_amountOpenMethodCalls.get(Thread.currentThread());
//        System.err.println("Logging internal call, current: "  + currentCalls + " updated= " + openMethodCalls);
        return openMethodCalls;
    }

    private static int decrementOpenMethodCalls() {
        int openMethodCalls;
        Integer currentCalls = thread_amountOpenMethodCalls.get(Thread.currentThread());
        Assert.notNull(currentCalls);
        int decremented = currentCalls - 1;
        //update
        thread_amountOpenMethodCalls.put(Thread.currentThread(), decremented);
        //get updated
        openMethodCalls = thread_amountOpenMethodCalls.get(Thread.currentThread());
//        System.err.println("Logging internal result, current: "  + currentCalls + " updated= " + openMethodCalls);
        return openMethodCalls;
    }


    private static void logResult(String method, String clazz, LogInteraction.Level level, boolean internal, Object ret) {
        int openMethodCalls = 0;
        if (internal) {
            openMethodCalls = decrementOpenMethodCalls();
        }

        String indentation = INDENTATION_CHAR.repeat((openMethodCalls) * INDENTATION_LENGTH);

        Logger logger = new Logger(level);
        logger.log("");
        String middlePart = "  OUTPUT of: " + clazz + "-" + method + getPaddedThreadInfoSuffix();
        String padding = createPadding(middlePart, SOFT_PADDING_CHAR);

        logger.log(indentation + padding + middlePart + padding);

        if (ret == null) {

            logger.log(indentation + "-> null" + getPaddedThreadInfoSuffix());

        } else {

            logger.log(indentation + "-> " + ret.toString() + getPaddedThreadInfoSuffix());

        }
        String endPadding = createPadding("", HARD_END_PADDING_CHAR);

        logger.log(indentation + endPadding + endPadding);
        logger.log("");
    }

    public static void logResult(String method, String clazz, LogInteraction.Level level, Object ret) {
        logResult(method, clazz, level, false, ret);
    }

    private static String getPaddedThreadInfoSuffix() {
        return "  ,Thread: " + Thread.currentThread().getId() + "  ";
    }

    private static String createPadding(String middlePart, String paddingChar) {
        int paddingLength = (LENGTH - middlePart.length()) / 2;
        return paddingChar.repeat(paddingLength);
    }

    private static LogInteraction extractAnnotation(JoinPoint joinPoint) {
        int paramCount = joinPoint.getArgs().length;
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(joinPoint.getTarget().getClass(), LogInteraction.class);
        Optional<Method> method = methods.stream().filter(m -> m.getName().equals(joinPoint.getSignature().getName())
                && m.getParameterCount() == paramCount
        ).findFirst();
        return method
                .map(value -> value.getAnnotation(LogInteraction.class))
                .orElse(null);
    }

    @Around("@annotation(com.github.vincemann.springrapid.core.advice.log.LogInteraction)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        LogInteraction logInteraction = extractAnnotation(joinPoint);
        if (!logWanted(logInteraction.level()))
            return joinPoint.proceed();

        String methodName = joinPoint.getSignature().getName();
        String clazzName = joinPoint.getTarget().getClass().getSimpleName();
        Object ret;
        if (logInteraction == null) {
            log.warn("Could not find LogInteraction annotation of method: " + methodName);
            logCall(methodName,
                    clazzName,
                    LogInteraction.Level.DEBUG,
                    true,
                    joinPoint.getArgs()
            );
            ret = joinPoint.proceed();
            logResult(
                    methodName,
                    clazzName,
                    LogInteraction.Level.DEBUG,
                    true,
                    ret
            );
        } else {
            logCall(methodName,
                    clazzName,
                    logInteraction.level(),
                    true,
                    joinPoint.getArgs()
            );
            ret = joinPoint.proceed();
            logResult(
                    methodName,
                    clazzName,
                    logInteraction.level(),
                    true,
                    ret
            );
        }
        return ret;
    }

    private static boolean logWanted(LogInteraction.Level level){
        switch (level){
            case TRACE:
                return log.isTraceEnabled();
            case ERROR:
                return log.isErrorEnabled();
            case DEBUG:
                return log.isDebugEnabled();
            case WARN:
                return log.isWarnEnabled();
            case INFO:
                return log.isInfoEnabled();
        }
        throw new IllegalStateException("Unknown log level");
    }

    @AfterThrowing("@annotation(com.github.vincemann.springrapid.core.advice.log.LogInteraction)")
    public void onException(JoinPoint joinPoint){
        LogInteraction logInteraction = extractAnnotation(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        String clazzName = joinPoint.getTarget().getClass().getSimpleName();
        logResult(
                methodName,
                clazzName,
                logInteraction.level(),
                true,
                "Exception thrown"
        );
    }

    @AllArgsConstructor
    private static class Logger {
        LogInteraction.Level level;

        private void log(String s) {
            switch (level) {
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
                case TRACE:
                    log.trace(s);
                    break;
            }
        }
    }

}
