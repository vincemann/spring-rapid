package io.github.vincemann.generic.crud.lib.test.testBundles.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public abstract class TestEntityBundle<E extends IdentifiableEntity> {
    private E entity;
}
