package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.requestEntityFactory;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration.abs.AbstractControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.BaseAddressProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.RequestEntity;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public abstract class RequestEntityFactory<Id extends Serializable> {
    private BaseAddressProvider baseAddressProvider;

    public abstract RequestEntity<?> create(AbstractControllerTestConfiguration<Id> config, Object body);

}
