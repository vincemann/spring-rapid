package io.github.vincemann.springrapid.core.service;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

import java.io.Serializable;
import java.util.Set;

@ServiceComponent
public interface ParentAwareCrudService
        <
                E extends IdentifiableEntity<?>,
                PId extends Serializable
        >
{

    public Set<E> findAllOfParent(PId parentId) throws BadEntityException;
}
