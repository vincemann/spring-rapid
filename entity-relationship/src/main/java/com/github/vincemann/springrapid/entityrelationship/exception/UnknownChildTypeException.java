package com.github.vincemann.springrapid.entityrelationship.exception;


import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.UniDirChild;

/**
 * Indicates that the searched {@link BiDirChild} / {@link UniDirChild} -
 * Type is unknown for the Parent, within it was searched.
 */
public class UnknownChildTypeException extends EntityRelationHandlingException {

    public UnknownChildTypeException(Class parentClass, Class unknownChildClass) {
        super("ChildClass : " + unknownChildClass.getSimpleName() + " is of unknown Type for parent with type " + parentClass.getSimpleName());
    }

}
