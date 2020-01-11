package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.save;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.AbstractTestConfiguration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SuccessfulSaveTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractTestConfiguration<E,Id>
{
    private EqualChecker<E> returnedEntityEqualChecker;

    @Builder
    public SuccessfulSaveTestConfiguration(EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) {
        super(repoEntityEqualChecker);
        this.returnedEntityEqualChecker = returnedEntityEqualChecker;
    }
}
