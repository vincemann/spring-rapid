package com.github.vincemann.springrapid.core.event;

import lombok.Builder;
import lombok.Getter;

import java.util.Collection;


@Getter
public class UpdateCollectionEvent {
    // detached
//    private Collection<?> old;
//    private Collection<?> updated;
    private String field;
    private Class<?> entityClass;
    private UpdateCollectionType type;

    public UpdateCollectionEvent(String field, Class<?> entityClass, UpdateCollectionType type) {
        this.field = field;
        this.entityClass = entityClass;
        this.type = type;
    }
}
