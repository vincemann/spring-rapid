package io.github.vincemann.generic.crud.lib.service.exception;

import io.github.vincemann.generic.crud.lib.service.springDataJpa.BiDirRelationManagingException;

public class UnknownParentTypeException extends BiDirRelationManagingException {


    public UnknownParentTypeException(Class childEntity, Class actualType){
        super("ParentEntity from ChildEntity "+childEntity.getSimpleName()+" was of unknown Type: " + actualType.getSimpleName());
    }
}
