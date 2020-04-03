package io.github.vincemann.demo.lib.controller;

import io.github.vincemann.generic.crud.lib.service.CrudService;
import org.springframework.data.repository.CrudRepository;

public interface ExampleService extends CrudService<ExampleEntity,Long, CrudRepository<ExampleEntity,Long>> {
}
