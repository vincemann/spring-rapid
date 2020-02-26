package io.github.vincemann.demo.controllers.springAdapter;

import io.github.vincemann.generic.crud.lib.service.CrudService;
import org.springframework.data.repository.CrudRepository;

public interface ExampleService extends CrudService<ExampleEntity,Long, CrudRepository<ExampleEntity,Long>> {
}
