package io.github.vincemann.generic.crud.lib.test.service.result;

import org.springframework.context.ApplicationContext;

public interface ServiceResultHandler {
    ServiceResultActions handle(ServiceResult serviceResult, ApplicationContext context);
}
