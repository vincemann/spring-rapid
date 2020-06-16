package com.github.vincemann.springrapid.core.advice.log;

public interface InteractionLogger {
     void logCall(String methodName, String clazz, LogInteraction.Level level, boolean internal, Object... args);
     void logResult(String method, String clazz, LogInteraction.Level level, boolean internal, Object ret);
}
