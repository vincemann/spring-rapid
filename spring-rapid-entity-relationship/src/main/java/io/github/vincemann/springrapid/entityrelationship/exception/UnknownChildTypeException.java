package io.github.vincemann.springrapid.entityrelationship.exception;


public class UnknownChildTypeException extends EntityRelationHandlingException {

    public UnknownChildTypeException(Class parentClass, Class unknownChildClass) {
        super("ChildClass : " + unknownChildClass.getSimpleName() + " is of unknown Type for parent with type " + parentClass.getSimpleName());
    }

}
