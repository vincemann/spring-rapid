package com.github.vincemann.springrapid.autobidir.exception;


/**
 * Indicates that the searched BiDirChild/ UniDirChild -
 * Type is unknown for the Parent, within it was searched.
 */
public class UnknownChildTypeException extends EntityRelationHandlingException {

    public UnknownChildTypeException(Class parentClass, Class unknownChildClass) {
        super("ChildClass : " + unknownChildClass.getSimpleName() + " is of unknown Type for parent with type " + parentClass.getSimpleName());
    }

}
