package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable> {
    private EqualChecker<E> repoEntityEqualChecker;
}
