package com.github.vincemann.springrapid.entityrelationship.exception;


import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;

/**
 * Indicates that the searched {@link BiDirParent} / {@link UniDirParent} -
 * Type is unknown for the Child, within it was searched.
 */
public class UnknownParentTypeException extends EntityRelationHandlingException {


    public UnknownParentTypeException(Class childEntity, Class actualType){
        super("ParentEntity from ChildEntity "+childEntity.getSimpleName()+" was of unknown Type: " + actualType.getSimpleName());
    }
}
