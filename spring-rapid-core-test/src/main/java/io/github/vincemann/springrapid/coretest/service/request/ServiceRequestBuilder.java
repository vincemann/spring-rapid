package io.github.vincemann.springrapid.coretest.service.request;

import io.github.vincemann.springrapid.core.service.CrudService;

public interface ServiceRequestBuilder {
    public ServiceRequest create(CrudService serviceUnderTest);
}
