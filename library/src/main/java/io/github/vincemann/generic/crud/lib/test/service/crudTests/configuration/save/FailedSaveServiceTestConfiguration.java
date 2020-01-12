package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.save;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.AbstractServiceTestConfiguration;

import java.io.Serializable;

public class FailedSaveServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractServiceTestConfiguration<E, Id> {
    public FailedSaveServiceTestConfiguration(EqualChecker<E> repoEntityEqualChecker) {
        super(repoEntityEqualChecker);
    }
}
