package io.github.vincemann.generic.crud.lib.service.exception;


public class UnknownChildTypeException extends BiDirRelationManagingException {

    public UnknownChildTypeException(Class parentClass, Class unknownChildClass) {
        super("ChildClass : " + unknownChildClass.getSimpleName() + " is of unknown Type for parent with type " + parentClass.getSimpleName());
    }

}
