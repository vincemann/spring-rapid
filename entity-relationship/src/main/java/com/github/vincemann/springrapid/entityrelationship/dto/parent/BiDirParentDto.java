package com.github.vincemann.springrapid.entityrelationship.dto.parent;

import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.entityrelationship.dto.DirDto;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a Dto, that has n Child Entities.
 * Each Child/ ChildCollection is represented by an id Field, annotated with {@link BiDirChildId} / {@link BiDirChildIdCollection}.
 * <p>
 * This Dto can be mapped to its Entity by using {@link IdResolvingDtoPostProcessor}
 */
public interface BiDirParentDto extends DirDto {

    Logger log = LoggerFactory.getLogger(BiDirParentDto.class);


//    default <ChildId extends Serializable> ChildId findBiDirChildId(Class<? extends BiDirChild> childClazz) throws UnknownChildTypeException {
//        return findChildId(childClazz,BiDirChildId.class);
//    }

    default Map<Class<BiDirChild>, Serializable> findBiDirChildIds() {
        return findEntityIds(BiDirChildId.class);
    }

//    default <ChildId extends Serializable> Collection<ChildId> findBiDirChildrenIdCollection(Class<? extends BiDirChild> childClazz)  {
//        return findChildIdCollection(childClazz,BiDirChildIdCollection.class);
//    }

    default Map<Class<BiDirChild>, Collection<Serializable>> findBiDirChildIdCollections() {
        return findEntityIdCollections(BiDirChildIdCollection.class);
    }




    /**
     * Adds childs id to {@link BiDirChildIdCollection} or {@link BiDirChildId}, depending on type it belongs to
     *
     * @param child
     */
    default void addBiDirChildId(BiDirChild child) {
        addEntityId(child, BiDirChildId.class, BiDirChildIdCollection.class);
    }
}
