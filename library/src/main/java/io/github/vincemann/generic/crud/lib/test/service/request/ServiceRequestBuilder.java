package io.github.vincemann.generic.crud.lib.test.service.request;

import io.github.vincemann.generic.crud.lib.service.CrudService;

public interface ServiceRequestBuilder {

    public ServiceRequest create(CrudService serviceUnderTest);
}
