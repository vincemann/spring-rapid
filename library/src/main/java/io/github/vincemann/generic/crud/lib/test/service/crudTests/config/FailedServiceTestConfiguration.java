package io.github.vincemann.generic.crud.lib.test.service.crudTests.config;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.abs.ServiceTestConfiguration;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class FailedServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable> extends ServiceTestConfiguration<E, Id> {
    private Class<? extends Throwable> expectedException;

    @Builder
    public FailedServiceTestConfiguration(EqualChecker<E> repoEntityEqualChecker, Class<? extends Throwable> expectedException) {
        super(repoEntityEqualChecker);
        this.expectedException = expectedException;
    }
}
