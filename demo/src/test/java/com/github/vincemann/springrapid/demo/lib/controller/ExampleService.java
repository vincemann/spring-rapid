package com.github.vincemann.springrapid.demo.lib.controller;

import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.data.repository.CrudRepository;

public interface ExampleService extends CrudService<ExampleEntity,Long> {
}
