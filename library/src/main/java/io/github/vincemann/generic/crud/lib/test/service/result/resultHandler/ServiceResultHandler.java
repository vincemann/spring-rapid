package io.github.vincemann.generic.crud.lib.test.service.result.resultHandler;

import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.action.ServiceResultActions;

public interface ServiceResultHandler {
    ServiceResultActions handle(ServiceResult serviceResult);
}
