package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

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
