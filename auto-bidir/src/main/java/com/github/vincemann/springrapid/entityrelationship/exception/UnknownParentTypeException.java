package com.github.vincemann.springrapid.entityrelationship.exception;


/**
 * Indicates that the searched BiDirParent / UniDirParent -
 * Type is unknown for the Child, within it was searched.
 */
public class UnknownParentTypeException extends EntityRelationHandlingException {


    public UnknownParentTypeException(Class childEntity, Class actualType){
        super("ParentEntity from ChildEntity "+childEntity.getSimpleName()+" was of unknown Type: " + actualType.getSimpleName());
    }
}
