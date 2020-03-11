package io.github.vincemann.generic.crud.lib.test.service.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;

@AllArgsConstructor
@Getter
@Builder
public class ServiceTestContext {
    private ApplicationContext applicationContext;
    private CrudRepository repository;
    private ServiceResult serviceResult;
}
