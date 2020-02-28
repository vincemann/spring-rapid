package io.github.vincemann.generic.crud.lib.test.service.result.handler;

import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.action.ServiceResultActions;
import org.springframework.context.ApplicationContext;

public interface ServiceResultHandler {
    ServiceResultActions handle(ServiceResult serviceResult, ApplicationContext context);
}
