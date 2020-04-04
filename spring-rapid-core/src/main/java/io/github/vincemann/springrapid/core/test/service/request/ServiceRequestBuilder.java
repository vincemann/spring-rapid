package io.github.vincemann.springrapid.core.test.service.request;

import io.github.vincemann.springrapid.core.service.CrudService;

public interface ServiceRequestBuilder {
    public ServiceRequest create(CrudService serviceUnderTest);
}
