package io.github.vincemann.generic.crud.lib.test.service.result;

import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;

public interface ServiceResultHandler {
    ServiceResultActions handle(ServiceTestContext context);
}
