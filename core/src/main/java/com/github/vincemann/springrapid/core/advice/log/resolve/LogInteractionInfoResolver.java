package com.github.vincemann.springrapid.core.advice.log.resolve;

import com.github.vincemann.springrapid.core.advice.log.LogInteractionInfo;
import org.aspectj.lang.reflect.MethodSignature;

public interface LogInteractionInfoResolver {

    public LogInteractionInfo resolve(Object target, MethodSignature methodSignature);
}
