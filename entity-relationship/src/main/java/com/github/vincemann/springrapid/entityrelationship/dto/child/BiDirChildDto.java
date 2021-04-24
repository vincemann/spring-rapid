package com.github.vincemann.springrapid.entityrelationship.dto.child;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.entityrelationship.dto.DirDto;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentIdCollection;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.DirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a Dto, that has n Parent Entities.
 * Each parent is represented by an id Field, annotated with {@link BiDirParentId}.
 * <p>
 * This Dto can be mapped to its Entity by using {@link IdResolvingDtoPostProcessor}
 */
public interface BiDirChildDto extends DirDto {

    default Map<Class<BiDirParent>, Serializable> findBiDirParentIds() {
        return findEntityIds(BiDirParentId.class);
    }

    default Map<Class<BiDirParent>, Collection<Serializable>> findBiDirParentIdCollections() {
        return findEntityIdCollections(BiDirParentIdCollection.class);
    }

    default void addBiDirParentId(BiDirParent biDirParent) {
        addEntityId((IdentifiableEntity) biDirParent,BiDirParentId.class,BiDirParentIdCollection.class);
    }

}
