package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.save;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.AbstractTestConfiguration;

import java.io.Serializable;

public class FailedSaveAbstractTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractTestConfiguration<E, Id> {
    public FailedSaveAbstractTestConfiguration(EqualChecker<E> repoEntityEqualChecker) {
        super(repoEntityEqualChecker);
    }
}
