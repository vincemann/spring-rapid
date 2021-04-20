package com.github.vincemann.springrapid.entityrelationship.dto.child;

import com.github.vincemann.springrapid.entityrelationship.dto.DirDto;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.UniDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.UniDirParentId;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.UniDirParentIdCollection;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.DirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.UniDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * See {@link BiDirChildDto}
 */
public interface UniDirChildDto extends DirDto {

    default Map<Class<UniDirParent>, Serializable> findUniDirParentIds()  {
        return findEntityIds(UniDirParentId.class);
    }

    default Map<Class<UniDirParent>, Collection<Serializable>> findUniDirParentIdCollections(){
        return findEntityIdCollections(UniDirParentIdCollection.class);
    }


    default void addUniDirParentId(UniDirParent uniDirParent) {
        addEntityId(uniDirParent,UniDirParentId.class, UniDirParentIdCollection.class);
    }

}
