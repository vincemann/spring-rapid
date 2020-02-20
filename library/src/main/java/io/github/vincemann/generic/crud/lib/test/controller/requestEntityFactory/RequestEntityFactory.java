package io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory;

import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import org.springframework.http.RequestEntity;

import java.io.Serializable;
public interface RequestEntityFactory<Id extends Serializable, C extends ControllerTestConfiguration<Id>> {
    RequestEntity<?> create(C config, Object body,Id id);

}
