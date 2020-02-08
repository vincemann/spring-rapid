package io.github.vincemann.generic.crud.lib.test.service.crudTests.config;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.abs.ServiceTestConfiguration;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class SuccessfulSaveServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends ServiceTestConfiguration<E,Id>
{
    private EqualChecker<E> returnedEntityEqualChecker;

    @Builder
    public SuccessfulSaveServiceTestConfiguration(EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) {
        super(repoEntityEqualChecker);
        this.returnedEntityEqualChecker = returnedEntityEqualChecker;
    }
}
