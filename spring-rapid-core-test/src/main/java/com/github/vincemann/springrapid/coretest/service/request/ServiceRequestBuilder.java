package com.github.vincemann.springrapid.coretest.service.request;

import com.github.vincemann.springrapid.core.service.CrudService;

public interface ServiceRequestBuilder {
    public ServiceRequest create(CrudService serviceUnderTest);
}
