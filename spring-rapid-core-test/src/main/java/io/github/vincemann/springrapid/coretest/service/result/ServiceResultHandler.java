package io.github.vincemann.springrapid.coretest.service.result;

public interface ServiceResultHandler {
    ServiceResultActions handle(ServiceTestContext context);
}
