package io.github.vincemann.generic.crud.lib.test.service.testApi.context;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DefaultTestContext<E extends IdentifiableEntity<Id>, Id extends Serializable> {
    private EqualChecker<E> repoEntityEqualChecker;
    private EqualChecker<E> returnedEntityEqualChecker;

    @Builder
    public DefaultTestContext(EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) {
        this.repoEntityEqualChecker = repoEntityEqualChecker;
        this.returnedEntityEqualChecker = returnedEntityEqualChecker;
    }
}
