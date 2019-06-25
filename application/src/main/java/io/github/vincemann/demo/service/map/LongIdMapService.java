package io.github.vincemann.demo.service.map;

import io.github.vincemann.demo.service.map.abs.MapService;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * Provides an Impl of {@link MapService}
 * Id must be of Tpe Long, thus {@param <E>} must extend BaseLongIdEntity
 * @param <E>       managed Entity Type
 */
public abstract class LongIdMapService<E extends IdentifiableEntity<Long>> extends MapService<E,Long> {
    @Override
    protected Long getNextId() {
        Long nextId = null;

        try {
            nextId = Collections.max(getMap().keySet()) + 1;
        } catch (NoSuchElementException e) {
            nextId = 1L;
        }

        return nextId;
    }
}
