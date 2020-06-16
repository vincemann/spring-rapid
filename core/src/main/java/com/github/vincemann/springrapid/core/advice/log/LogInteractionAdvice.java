package com.github.vincemann.springrapid.core.advice.log;

import com.github.vincemann.springrapid.core.advice.log.resolve.LogInteractionInfoResolver;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
@Slf4j
public class LogInteractionAdvice {

    private InteractionLogger logger;
    private LogInteractionInfoResolver infoResolver;


    public LogInteractionAdvice(InteractionLogger logger, LogInteractionInfoResolver infoResolver) {
        this.logger = logger;
        this.infoResolver = infoResolver;
    }
    

    @Around("this(com.github.vincemann.springrapid.core.advice.log.AopLoggable)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogInteractionInfo logInfo = infoResolver.resolve(joinPoint.getTarget(),signature);


        if (logInfo==null)
            return joinPoint.proceed();

        LogInteraction logInteraction = logInfo.getAnnotation();
        LogConfig logConfig = extractConfig(logInfo);

        if (!logWanted(logInteraction.level()))
            return joinPoint.proceed();
        if (!methodWanted(logConfig,logInfo,signature))
            return joinPoint.proceed();

        String methodName = signature.getName();
        String clazzName = joinPoint.getTarget().getClass().getSimpleName();

        Object ret;
            logger.logCall(methodName,
                    clazzName,
                    logInteraction.level(),
                    true,
                    joinPoint.getArgs()
            );
            ret = joinPoint.proceed();
            logger.logResult(
                    methodName,
                    clazzName,
                    logInteraction.level(),
                    true,
                    ret
            );
        return ret;
    }



    private static boolean methodWanted(LogConfig logConfig,LogInteractionInfo LogInteractionInfo, MethodSignature signature){
        if (logConfig==null){
            return true;
        }
        return !(logConfig.ignoreGetters()
                && (signature.getName().startsWith("get") || signature.getName().startsWith("is"))
                && LogInteractionInfo.isClassLevel()
                )
                    &&
                !(logConfig.ignoreSetters()
                && (signature.getName().startsWith("set"))
                && LogInteractionInfo.isClassLevel()
                );
    }

    private static LogConfig extractConfig(LogInteractionInfo LogInteractionInfo){
        if (LogInteractionInfo.isFromInterface()){
            return LogInteractionInfo.getTargetClass().getAnnotation(LogConfig.class);
        }else  {
            return LogInteractionInfo.getTargetClass().getDeclaredAnnotation(LogConfig.class);
        }
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
