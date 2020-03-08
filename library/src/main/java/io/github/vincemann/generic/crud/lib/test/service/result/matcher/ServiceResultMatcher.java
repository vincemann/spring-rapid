package io.github.vincemann.generic.crud.lib.test.service.result.matcher;

import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import org.springframework.context.ApplicationContext;

public interface ServiceResultMatcher {
    public void match(ServiceResult serviceResult, ApplicationContext applicationContext);
}
