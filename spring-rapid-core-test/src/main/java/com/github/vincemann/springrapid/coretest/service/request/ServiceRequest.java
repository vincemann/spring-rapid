package com.github.vincemann.springrapid.coretest.service.request;

import com.github.vincemann.springrapid.core.service.CrudService;
import lombok.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Represents an invocation of {@link CrudService} method in a test scenario.
 */
@Getter
@Setter
@NoArgsConstructor
public class ServiceRequest {
    private CrudService service;
    private Method serviceMethod;
    private List<Object> args;

    @Builder
    public ServiceRequest(Method serviceMethod, List<Object> args) {
        this.serviceMethod = serviceMethod;
        this.args = args;
    }
}
