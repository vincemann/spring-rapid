package com.github.vincemann.springrapid.entityrelationship.exception;


/**
 * Indicates that the searched {@link com.github.vincemann.springrapid.entityrelationship.model.biDir.child.BiDirChild} / {@link com.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChild} -
 * Type is unknown for the Parent, within it was searched.
 */
public class UnknownChildTypeException extends EntityRelationHandlingException {

    public UnknownChildTypeException(Class parentClass, Class unknownChildClass) {
        super("ChildClass : " + unknownChildClass.getSimpleName() + " is of unknown Type for parent with type " + parentClass.getSimpleName());
    }

}
