package com.github.vincemann.springrapid.demo.lib.controller;

import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import org.springframework.data.repository.CrudRepository;

public interface ExampleService extends AbstractCrudService<ExampleEntity,Long, CrudRepository<ExampleEntity,Long>> {
}
