package io.github.vincemann.generic.crud.lib.test.service.testApi.configuration;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.testApi.configuration.abs.AbstractTestConfiguration;

import java.io.Serializable;

public class SuccessfulSaveTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractTestConfiguration<E,Id>
{

    public SuccessfulSaveTestConfiguration(EqualChecker<E> repoEntityEqualChecker) {
        super(repoEntityEqualChecker);
    }
}
