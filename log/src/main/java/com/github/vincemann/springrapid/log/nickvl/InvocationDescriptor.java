/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package com.github.vincemann.springrapid.log.nickvl;

import com.github.vincemann.springrapid.log.nickvl.annotation.*;
import org.springframework.lang.Nullable;

/**
 * Method descriptor.
 */
final class InvocationDescriptor {
    private final Severity beforeSeverity;
    private final Severity afterSeverity;
    @Nullable
    private final LogException exceptionAnnotation;

    private InvocationDescriptor(Severity beforeSeverity, Severity afterSeverity,@Nullable LogException exceptionAnnotation) {
        this.beforeSeverity = beforeSeverity;
        this.afterSeverity = afterSeverity;
        this.exceptionAnnotation = exceptionAnnotation;
    }

    public Severity getBeforeSeverity() {
        return beforeSeverity;
    }

    public Severity getAfterSeverity() {
        return afterSeverity;
    }

    public LogException getExceptionAnnotation() {
        return exceptionAnnotation;
    }

    /**
     * Builder.
     */
    public static final class Builder {
        private AnnotationInfo<Logging> loggingInfo;
        private AnnotationInfo<LogException> logExceptionInfo;
        private Severity beforeSeverity;
        private Severity afterSeverity;
        private Severity defaultSeverity;
        private Severity classBeforeSeverity;
        private Severity classAfterSeverity;
        private Severity classDefaultSeverity;

        public Builder(@Nullable AnnotationInfo<Logging> loggingInfo, @Nullable AnnotationInfo<LogException> logExceptionInfo) {
            this.loggingInfo = loggingInfo;
            this.logExceptionInfo = logExceptionInfo;
        }

        public InvocationDescriptor build() {
            LogException logException = logExceptionInfo== null ? null : logExceptionInfo.getAnnotation();
            parseSeverity(loggingInfo);


            if (Utils.hasNotNull(beforeSeverity, defaultSeverity, afterSeverity)) {
                return new InvocationDescriptor(
                        Utils.coalesce(beforeSeverity, defaultSeverity),
                        Utils.coalesce(afterSeverity, defaultSeverity),
                        logException
                );
            }

            return new InvocationDescriptor(
                    Utils.coalesce(classBeforeSeverity, classDefaultSeverity),
                    Utils.coalesce(classAfterSeverity, classDefaultSeverity),
                    logException
            );

        }

        private void parseSeverity(AnnotationInfo<Logging> loggingInfo) {
            if (loggingInfo==null){
                return;
            }
            Logging annotation = loggingInfo.getAnnotation();
//            LogException logExceptionAnnotation = null;

               /* if (currAnnotation.annotationType().equals(LogException.class)) {
                    logExceptionAnnotation = (LogException) currAnnotation;
                } else */
            if (annotation.annotationType().equals(LogFatal.class)) {
                LogFatal logAnnotation = (LogFatal) annotation;
                setSeverity(logAnnotation.value(), Severity.FATAL, !loggingInfo.isClassLevel());
            } else if (annotation.annotationType().equals(LogError.class)) {
                LogError logAnnotation = (LogError) annotation;
                setSeverity(logAnnotation.value(), Severity.ERROR, !loggingInfo.isClassLevel());
            } else if (annotation.annotationType().equals(LogWarn.class)) {
                LogWarn logAnnotation = (LogWarn) annotation;
                setSeverity(logAnnotation.value(), Severity.WARN, !loggingInfo.isClassLevel());
            } else if (annotation.annotationType().equals(LogInfo.class)) {
                LogInfo logAnnotation = (LogInfo) annotation;
                setSeverity(logAnnotation.value(), Severity.INFO, !loggingInfo.isClassLevel());
            } else if (annotation.annotationType().equals(LogDebug.class)) {
                LogDebug logAnnotation = (LogDebug) annotation;
                setSeverity(logAnnotation.value(), Severity.DEBUG, !loggingInfo.isClassLevel());
            } else if (annotation.annotationType().equals(LogTrace.class)) {
                LogTrace logAnnotation = (LogTrace) annotation;
                setSeverity(logAnnotation.value(), Severity.TRACE, !loggingInfo.isClassLevel());
            }
        }

        private void setSeverity(LogPoint logPoint, Severity targetSeverity, boolean fromMethod) {
            if (fromMethod) {
                if (logPoint == LogPoint.IN) {
                    beforeSeverity = Utils.max(targetSeverity, beforeSeverity);
                } else if (logPoint == LogPoint.OUT) {
                    afterSeverity = Utils.max(targetSeverity, afterSeverity);
                } else if (logPoint == LogPoint.BOTH) {
                    defaultSeverity = Utils.max(targetSeverity, defaultSeverity);
                }
            } else {
                if (logPoint == LogPoint.IN) {
                    classBeforeSeverity = Utils.max(targetSeverity, classBeforeSeverity);
                } else if (logPoint == LogPoint.OUT) {
                    classAfterSeverity = Utils.max(targetSeverity, classAfterSeverity);
                } else if (logPoint == LogPoint.BOTH) {
                    classDefaultSeverity = Utils.max(targetSeverity, classDefaultSeverity);
                }
            }
        }

    }
}
