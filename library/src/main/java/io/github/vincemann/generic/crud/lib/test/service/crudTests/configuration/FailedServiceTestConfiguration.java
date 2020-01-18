package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.ServiceTestConfiguration;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class FailedServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable> extends ServiceTestConfiguration<E, Id> {
    private Class<? extends Throwable> expectedException;

    @Builder
    public FailedServiceTestConfiguration(EqualChecker<E> repoEntityEqualChecker, Class<? extends Throwable> expectedException) {
        super(repoEntityEqualChecker);
        this.expectedException = expectedException;
    }
}
