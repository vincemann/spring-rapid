package com.github.vincemann.springrapid.log.nickvl;

import com.github.vincemann.springrapid.log.nickvl.annotation.LogConfig;
import com.github.vincemann.springrapid.log.nickvl.annotation.LogException;
import com.github.vincemann.springrapid.log.nickvl.annotation.Logging;
import com.github.vincemann.springrapid.log.nickvl.rapid.LogInteraction;
import com.github.vincemann.springrapid.log.nickvl.rapid.resolve.AnnotationParser;
import org.apache.commons.logging.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Based on {@link AOPLogger}.

 */
@Aspect
public class ProxyAwareAopLogger implements InitializingBean {

    // private static final Log LOGGER = LogFactory.getLog(AOPLogger.class);
    private LogAdapter logAdapter;
    private Map<Severity, LogStrategy> logStrategies;
    private final LocalVariableTableParameterNameDiscoverer localVariableNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private final ExceptionResolver exceptionResolver = new ExceptionResolver();
    private final ConcurrentMap<Method, MethodDescriptor> cache = new ConcurrentHashMap<Method, MethodDescriptor>();
    private AnnotationParser annotationParser;

    @Override
    public void afterPropertiesSet() throws Exception {
        logStrategies = new EnumMap<Severity, LogStrategy>(Severity.class);
        logStrategies.put(Severity.FATAL, new LogStrategy.FatalLogStrategy(logAdapter));
        logStrategies.put(Severity.ERROR, new LogStrategy.ErrorLogStrategy(logAdapter));
        logStrategies.put(Severity.WARN, new LogStrategy.WarnLogStrategy(logAdapter));
        logStrategies.put(Severity.INFO, new LogStrategy.InfoLogStrategy(logAdapter));
        logStrategies.put(Severity.DEBUG, new LogStrategy.DebugLogStrategy(logAdapter));
        logStrategies.put(Severity.TRACE, new LogStrategy.TraceLogStrategy(logAdapter));
    }

    public void setLogAdapter(LogAdapter log) {
        this.logAdapter = log;
    }



    public ProxyAwareAopLogger(AnnotationParser annotationParser) {
        this.annotationParser = annotationParser;
    }
    

    @Around("this(com.github.vincemann.springrapid.log.nickvl.rapid.InteractionLoggable)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        AnnotationInfo<Logging> logInfo = annotationParser.fromMethodOrClass(signature.getMethod(),Logging.class);
        AnnotationInfo<LogException> logExceptionInfo = annotationParser.fromMethodOrClass(signature.getMethod(),LogException.class);
        LogConfig logConfig  = annotationParser.fromClass(targetClass,LogConfig.class);


        Object[] args = joinPoint.getArgs();
        Log logger = logAdapter.getLog(joinPoint.getTarget().getClass());
        Method method = extractMethod(joinPoint);

        MethodDescriptor descriptor = getMethodDescriptor(method);
        InvocationDescriptor invocationDescriptor = descriptor.getInvocationDescriptor();

        String methodName = method.getName();

        if (beforeLoggingOn(invocationDescriptor, logger)) {
            ArgumentDescriptor argumentDescriptor = getArgumentDescriptor(descriptor, method, args.length);
            logStrategies.get(invocationDescriptor.getBeforeSeverity()).logBefore(logger, methodName, args, argumentDescriptor);
        }

        Object result;
        if (invocationDescriptor.getExceptionAnnotation() == null) {
            result = joinPoint.proceed(args);
        } else {
            try {
                result = joinPoint.proceed(args);
            } catch (Exception e) {
                ExceptionDescriptor exceptionDescriptor = getExceptionDescriptor(descriptor, invocationDescriptor);
                Class<? extends Exception> resolved = exceptionResolver.resolve(exceptionDescriptor, e);
                if (resolved != null) {
                    ExceptionSeverity excSeverity = exceptionDescriptor.getExceptionSeverity(resolved);
                    if (isLoggingOn(excSeverity.getSeverity(), logger)) {
                        logStrategies.get(excSeverity.getSeverity()).logException(logger, methodName, args.length, e, excSeverity.getStackTrace());
                    }
                }
                throw e;
            }
        }
        if (afterLoggingOn(invocationDescriptor, logger)) {
            Object loggedResult = (method.getReturnType() == Void.TYPE) ? Void.TYPE : result;
            logStrategies.get(invocationDescriptor.getAfterSeverity()).logAfter(logger, methodName, args.length, loggedResult);
        }
        return result;



//        if (logInfo==null)
//            return joinPoint.proceed();
//
//        LogInteraction logInteraction = logInfo.getAnnotation();
//        LogConfig logConfig = extractConfig(logInfo);
//
//        if (!logWanted(logInteraction.level()))
//            return joinPoint.proceed();
//        if (!methodWanted(logConfig,logInfo,signature))
//            return joinPoint.proceed();
//
//        String methodName = signature.getName();
//        String clazzName = joinPoint.getTarget().getClass().getSimpleName();
//
//        Object ret;
//            logger.logCall(methodName,
//                    clazzName,
//                    logInteraction.level(),
//                    true,
//                    joinPoint.getArgs()
//            );
//            ret = joinPoint.proceed();
//            logger.logResult(
//                    methodName,
//                    clazzName,
//                    logInteraction.level(),
//                    true,
//                    ret
//            );
//        return ret;
    }



    private static boolean methodWanted(LogConfig logConfig, AnnotationInfo AnnotationInfo, MethodSignature signature){
        if (logConfig==null){
            return true;
        }
        return !(logConfig.ignoreGetters()
                && (signature.getName().startsWith("get") || signature.getName().startsWith("is"))
                && AnnotationInfo.isClassLevel()
                )
                    &&
                !(logConfig.ignoreSetters()
                && (signature.getName().startsWith("set"))
                && AnnotationInfo.isClassLevel()
                );
    }

    private static LogConfig extractConfig(AnnotationInfo AnnotationInfo){
        if (AnnotationInfo.isFromInterface()){
            return AnnotationInfo.getTargetClass().getAnnotation(LogConfig.class);
        }else  {
            return AnnotationInfo.getTargetClass().getDeclaredAnnotation(LogConfig.class);
        }
    }




    /**
     * Advise. Logs the advised method.
     *
     * @param joinPoint represents advised method
     * @return method execution result
     * @throws Throwable in case of exception
     */
    @Around(value = "execution(@(@com.github.vincemann.springrapid.log.nickvl.annotation.Logging *) * *.* (..))" // per method
            + " || execution(* (@(@com.github.vincemann.springrapid.log.nickvl.annotation.Logging *) *).*(..))"  // per class
            , argNames = "joinPoint")
    public Object logTheMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        return result;
    }

    private MethodDescriptor getMethodDescriptor(Method method) {
        MethodDescriptor cached = cache.get(method);
        if (cached != null) {
            return cached;
        }
        cached = new MethodDescriptor(new InvocationDescriptor.Builder(method).build());
        MethodDescriptor prev = cache.putIfAbsent(method, cached);
        return prev == null ? cached : prev;
    }

    private ArgumentDescriptor getArgumentDescriptor(MethodDescriptor descriptor, Method method, int argumentCount) {
        if (descriptor.getArgumentDescriptor() != null) {
            return descriptor.getArgumentDescriptor();
        }
        ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor.Builder(method, argumentCount, localVariableNameDiscoverer).build();
        descriptor.setArgumentDescriptor(argumentDescriptor);
        return argumentDescriptor;
    }

    private ExceptionDescriptor getExceptionDescriptor(MethodDescriptor descriptor, InvocationDescriptor invocationDescriptor) {
        if (descriptor.getExceptionDescriptor() != null) {
            return descriptor.getExceptionDescriptor();
        }
        ExceptionDescriptor exceptionDescriptor = new ExceptionDescriptor.Builder(invocationDescriptor.getExceptionAnnotation()).build();
        descriptor.setExceptionDescriptor(exceptionDescriptor);
        return exceptionDescriptor;
    }

    private Method extractMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // signature.getMethod() points to method declared in interface. it is not suit to discover arg names and arg annotations
        // see AopProxyUtils: org.springframework.cache.interceptor.CacheAspectSupport#execute(CacheAspectSupport.Invoker, Object, Method, Object[])
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (Modifier.isPublic(signature.getMethod().getModifiers())) {
            return targetClass.getMethod(signature.getName(), signature.getParameterTypes());
        } else {
            return ReflectionUtils.findMethod(targetClass, signature.getName(), signature.getParameterTypes());
        }
    }

    private boolean beforeLoggingOn(InvocationDescriptor descriptor, Log logger) {
        return isLoggingOn(descriptor.getBeforeSeverity(), logger);
    }

    private boolean afterLoggingOn(InvocationDescriptor descriptor, Log logger) {
        return isLoggingOn(descriptor.getAfterSeverity(), logger);
    }

    private boolean isLoggingOn(Severity severity, Log logger) {
        return severity != null && logStrategies.get(severity).isLogEnabled(logger);
    }


//    private static boolean logWanted(LogInteraction.Level level){
//        switch (level){
//            case TRACE:
//                return log.isTraceEnabled();
//            case ERROR:
//                return log.isErrorEnabled();
//            case DEBUG:
//                return log.isDebugEnabled();
//            case WARN:
//                return log.isWarnEnabled();
//            case INFO:
//                return log.isInfoEnabled();
//        }
//        throw new IllegalStateException("Unknown log level");
//    }

//    @AfterThrowing("target(com.github.vincemann.springrapid.core.advice.log.InteractionLoggable)"/*"@annotation(com.github.vincemann.springrapid.core.advice.log.LogInteraction)"*/)
//    public void onException(JoinPoint joinPoint){
//        LogInteraction logInteraction = extractAnnotation(joinPoint);
//        if (logInteraction==null)
//            return;
//        if (!logWanted(logInteraction.level()))
//            return;
//
//
//        String methodName = joinPoint.getSignature().getName();
//        String clazzName = joinPoint.getTarget().getClass().getSimpleName();
//        logResult(
//                methodName,
//                clazzName,
//                logInteraction.level(),
//                true,
//                "Exception thrown"
//        );
//    }

   

}
