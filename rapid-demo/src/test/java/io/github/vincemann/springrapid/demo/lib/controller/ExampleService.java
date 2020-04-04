package io.github.vincemann.springrapid.demo.lib.controller;

import io.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.data.repository.CrudRepository;

public interface ExampleService extends CrudService<ExampleEntity,Long, CrudRepository<ExampleEntity,Long>> {
}
