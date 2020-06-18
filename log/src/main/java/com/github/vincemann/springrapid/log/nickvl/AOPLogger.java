package com.github.vincemann.springrapid.log.nickvl;

import com.github.vincemann.springrapid.log.nickvl.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

    @Around("this(com.github.vincemann.springrapid.log.nickvl.annotation.AopLoggable)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        LoggedMethodCall loggedCall = new LoggedMethodCall(joinPoint);

        if (!loggedCall.methodWanted() || !loggedCall.isLoggingOn()){
            return loggedCall.proceed();
        }

        if (loggedCall.isInvocationLoggingOn()) {
            loggedCall.logInvocation();
        }

        if (!loggedCall.isExceptionLoggingOn()) {
            loggedCall.proceed();
        } else {
            try {
                loggedCall.proceed();
            } catch (Exception e) {
                loggedCall.logException(e);
                throw e;
            }
        }

        if (loggedCall.isResultLoggingOn()) {
            loggedCall.logResult();
        }
        return loggedCall.getResult();
    }

    @Getter
    @AllArgsConstructor
    public class LoggedMethodCall{
        Method method;
        Class<?> targetClass;
        Object[] args;
        AnnotationInfo<Log> logInfo;
        AnnotationInfo<LogException> logExceptionInfo;
        LogConfig logConfig;
        MethodDescriptor methodDescriptor;
        InvocationDescriptor invocationDescriptor;
        ArgumentDescriptor argumentDescriptor;
        ProceedingJoinPoint joinPoint;
        org.apache.commons.logging.Log logger;

        Object result;

        LoggedMethodCall(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
            this.joinPoint = joinPoint;
            this.method = extractMethod(joinPoint);
            this.targetClass = joinPoint.getTarget().getClass();
            this.logInfo = annotationParser.fromMethodOrClass(method, Log.class);
            this.logExceptionInfo = annotationParser.fromMethodOrClass(method,LogException.class);
            this.logConfig  =  extractConfig(logInfo);
            this.methodDescriptor = evalMethodDescriptor(method,logInfo,logExceptionInfo);
            this.invocationDescriptor = methodDescriptor.getInvocationDescriptor();
            this.args = joinPoint.getArgs();
            this.logger = logAdapter.getLog(targetClass);
            this.argumentDescriptor=evalArgumentDescriptor(methodDescriptor,method,args.length);
        }


        boolean methodWanted(){
            if (logConfig!=null){
                if (logConfig.ignoreGetters() && (method.getName().startsWith("get") || method.getName().startsWith("is"))){
                    return false;
                }
                if (logConfig.ignoreSetters() && (method.getName().startsWith("set"))){
                    return false;
                }
            }
            if (logInfo==null){
                return false;
            }
            if (logInfo.getAnnotation().disabled()){
                return false;
            }
            return true;
        }

        Object proceed() throws Throwable {
            result = joinPoint.proceed(args);
            return result;
        }

        void logException(Exception e){
            ExceptionDescriptor exceptionDescriptor = evalExceptionDescriptor(methodDescriptor, invocationDescriptor);
            Class<? extends Exception> resolved = exceptionResolver.resolve(exceptionDescriptor, e);
            if (resolved != null) {
                ExceptionSeverity excSeverity = exceptionDescriptor.getExceptionSeverity(resolved);
                if (isLoggingOn(excSeverity.getSeverity())) {
                    logStrategies.get(excSeverity.getSeverity()).logException(logger, method.getName(), args.length, e, excSeverity.getStackTrace());
                }
            }
        }

        void logInvocation(){
            logStrategies.get(invocationDescriptor.getBeforeSeverity())
                    .logBefore(logger, method.getName(), args, argumentDescriptor);
        }

        void logResult(){
            Object loggedResult = (method.getReturnType() == Void.TYPE) ? Void.TYPE : result;
            logStrategies.get(invocationDescriptor.getAfterSeverity()).logAfter(logger, method.getName(), args.length, loggedResult);
        }

        boolean isInvocationLoggingOn() {
            return isLoggingOn(invocationDescriptor.getBeforeSeverity());
        }

        boolean isResultLoggingOn() {
            return isLoggingOn(invocationDescriptor.getAfterSeverity());
        }

        boolean isExceptionLoggingOn(){
            if (logExceptionInfo==null){
                return false;
            }
            return (logExceptionInfo.getAnnotation().value().length>0 && isLoggingOn(Severity.ERROR))||
                    (logExceptionInfo.getAnnotation().fatal().length>0 && isLoggingOn(Severity.FATAL)) ||
                    (logExceptionInfo.getAnnotation().trace().length>0 && isLoggingOn(Severity.TRACE))||
                    (logExceptionInfo.getAnnotation().debug().length>0 && isLoggingOn(Severity.DEBUG))||
                    (logExceptionInfo.getAnnotation().info().length>0 && isLoggingOn(Severity.INFO))||
                    (logExceptionInfo.getAnnotation().warn().length>0 && isLoggingOn(Severity.WARN));
        }

        boolean isLoggingOn(){
            return isInvocationLoggingOn() || isResultLoggingOn() || isExceptionLoggingOn();
        }

        private boolean isLoggingOn(Severity severity) {
            return severity != null && logStrategies.get(severity).isLogEnabled(logger);
        }
    }


    //config is only valid if present on same class as class level Logging annotation
    protected LogConfig extractConfig(AnnotationInfo<Log> loggingInfo){
        if (loggingInfo==null){
            return null;
        }
        if (loggingInfo.isClassLevel()){
            return loggingInfo.getTargetClass().getDeclaredAnnotation(LogConfig.class);
        }else {
            return null;
        }
    }



    private MethodDescriptor evalMethodDescriptor(Method method, AnnotationInfo<Log> loggingInfo, @Nullable AnnotationInfo<LogException> logExceptionInfo) {
        MethodDescriptor cached = cache.get(method);
        if (cached != null) {
            return cached;
        }
        cached = new MethodDescriptor(new InvocationDescriptor.Builder(loggingInfo,logExceptionInfo).build());
        MethodDescriptor prev = cache.putIfAbsent(method, cached);
        return prev == null ? cached : prev;
    }

    private ArgumentDescriptor evalArgumentDescriptor(MethodDescriptor descriptor, Method method, int argumentCount) {
        if (descriptor.getArgumentDescriptor() != null) {
            return descriptor.getArgumentDescriptor();
        }
        ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor.Builder(method, argumentCount, localVariableNameDiscoverer).build();
        descriptor.setArgumentDescriptor(argumentDescriptor);
        return argumentDescriptor;
    }

    private ExceptionDescriptor evalExceptionDescriptor(MethodDescriptor descriptor, InvocationDescriptor invocationDescriptor) {
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


}
