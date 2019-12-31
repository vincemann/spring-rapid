package io.github.vincemann.generic.crud.lib.service.exception.entityRelationHandling;


import io.github.vincemann.generic.crud.lib.service.exception.entityRelationHandling.EntityRelationHandlingException;

public class UnknownChildTypeException extends EntityRelationHandlingException {

    public UnknownChildTypeException(Class parentClass, Class unknownChildClass) {
        super("ChildClass : " + unknownChildClass.getSimpleName() + " is of unknown Type for parent with type " + parentClass.getSimpleName());
    }

}
