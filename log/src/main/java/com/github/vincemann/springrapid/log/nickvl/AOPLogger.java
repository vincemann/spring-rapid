package com.github.vincemann.springrapid.log.nickvl;

import com.github.vincemann.springrapid.log.nickvl.annotation.LogConfig;
import com.github.vincemann.springrapid.log.nickvl.annotation.LogException;
import com.github.vincemann.springrapid.log.nickvl.annotation.Logging;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.commons.logging.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Aspect
public class AOPLogger implements InitializingBean {

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



    public AOPLogger(AnnotationParser annotationParser) {
        this.annotationParser = annotationParser;
    }

    @Builder
    @AllArgsConstructor
    public class LoggedMethodCall{
        Method method;
        Class<?> targetClass;
        Object[] args;

        AnnotationInfo<Logging> logInfo;
        AnnotationInfo<LogException> logExceptionInfo;
        LogConfig logConfig;

        MethodDescriptor methodDescriptor;
        InvocationDescriptor invocationDescriptor;
        ArgumentDescriptor argumentDescriptor;

        Log logger;

        LoggedMethodCall(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
            method = extractMethod(joinPoint);
            targetClass = joinPoint.getTarget().getClass();
            logInfo = annotationParser.fromMethodOrClass(method,Logging.class);
            logExceptionInfo = annotationParser.fromMethodOrClass(method,LogException.class);
            logConfig  =  extractConfig(logInfo);
            methodDescriptor = getMethodDescriptor(method,logInfo,logExceptionInfo);
            invocationDescriptor = methodDescriptor.getInvocationDescriptor();
            args = joinPoint.getArgs();
            logger = logAdapter.getLog(targetClass);
        }

        boolean logBefore(){

        }

        void logCall(){
            logStrategies.get(invocationDescriptor.getBeforeSeverity()).logBefore(logger, method.getName(), args, argumentDescriptor);
        }



    }
    

    @Around("this(com.github.vincemann.springrapid.log.nickvl.annotation.InteractionLoggable)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {



        if (!methodWanted(logConfig,methodName))
            return joinPoint.proceed();
        if (!logWanted(logInfo)){
            return
        }





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

                throw e;
            }
        }
        if (afterLoggingOn(invocationDescriptor, logger)) {
            Object loggedResult = (method.getReturnType() == Void.TYPE) ? Void.TYPE : result;
            logStrategies.get(invocationDescriptor.getAfterSeverity()).logAfter(logger, methodName, args.length, loggedResult);
        }
        return result;
    }

    protected void logException(Exception e, MethodDescriptor descriptor,InvocationDescriptor invocationDescriptor, Log logger,String methodName,Object[] args){
        ExceptionDescriptor exceptionDescriptor = getExceptionDescriptor(descriptor, invocationDescriptor);
        Class<? extends Exception> resolved = exceptionResolver.resolve(exceptionDescriptor, e);
        if (resolved != null) {
            ExceptionSeverity excSeverity = exceptionDescriptor.getExceptionSeverity(resolved);
            if (isLoggingOn(excSeverity.getSeverity(), logger)) {
                logStrategies.get(excSeverity.getSeverity()).logException(logger, methodName, args.length, e, excSeverity.getStackTrace());
            }
        }
    }

    //config is only valid if present on same class as class level Logging annotation
    protected LogConfig extractConfig(AnnotationInfo<Logging> loggingInfo){
        if (loggingInfo.isClassLevel()){
            return loggingInfo.getTargetClass().getDeclaredAnnotation(LogConfig.class);
        }else {
            return null;
        }
    }


    private static boolean methodWanted(LogConfig logConfig, String methodName){
        if (logConfig==null){
            return true;
        }
        //are getters & setters ignored ?
        return !(logConfig.ignoreGetters()
                && (methodName.startsWith("get") || methodName.startsWith("is"))
                )
                    &&
                !(logConfig.ignoreSetters()
                && (methodName.startsWith("set"))
                );
    }

    private MethodDescriptor getMethodDescriptor(Method method,AnnotationInfo<Logging> loggingInfo, @Nullable AnnotationInfo<LogException> logExceptionInfo) {
        MethodDescriptor cached = cache.get(method);
        if (cached != null) {
            return cached;
        }
        cached = new MethodDescriptor(new InvocationDescriptor.Builder(loggingInfo,logExceptionInfo).build());
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

}
