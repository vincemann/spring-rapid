package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.aop.TargetClassAware;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * Interface for a Service offering Crud Operations.
 * @param <E>       Type of Entity which's crud operations are exposed by this Service
 * @param <Id>      Id Type of E
 */
@ServiceComponent
public interface CrudService
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E,Id>
        >
    extends  SimpleCrudService<E,Id>, TargetClassAware
{
    R getRepository();
}