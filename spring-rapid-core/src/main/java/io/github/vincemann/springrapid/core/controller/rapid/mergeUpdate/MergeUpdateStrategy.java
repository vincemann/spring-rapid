package io.github.vincemann.springrapid.core.controller.rapid.mergeUpdate;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface MergeUpdateStrategy<E extends IdentifiableEntity<?>> {

    public E merge(E patch, E saved, Class<?> dtoClass) throws BadEntityException;
}
