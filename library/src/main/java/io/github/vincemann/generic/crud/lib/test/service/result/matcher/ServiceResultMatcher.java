package io.github.vincemann.generic.crud.lib.test.service.result.matcher;

import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;

public interface ServiceResultMatcher {
    public void match(ServiceResult serviceResult, ApplicationContext applicationContext);
}
