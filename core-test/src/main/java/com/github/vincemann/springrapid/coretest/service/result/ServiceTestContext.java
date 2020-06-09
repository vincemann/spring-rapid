package com.github.vincemann.springrapid.coretest.service.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;

/**
 * Represent the context of a test created and executed with {@link com.github.vincemann.springrapid.coretest.service.ServiceTestTemplate}.
 */
@AllArgsConstructor
@Getter
@Builder
public class ServiceTestContext {
    private ApplicationContext applicationContext;
    private CrudRepository repository;
    private ServiceResult serviceResult;
}
