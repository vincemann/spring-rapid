package com.github.vincemann.springrapid.core.advice.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

@Builder
@AllArgsConstructor
@Getter
public class LogInteractionInfo {
    private LogInteraction annotation;
    private boolean classLevel;
    @Nullable
    private Method method;
    private Class<?> targetClass;
    private boolean fromInterface;
}
