package io.github.vincemann.springrapid.core.service.exception.entityRelationHandling;


public class UnknownParentTypeException extends EntityRelationHandlingException {


    public UnknownParentTypeException(Class childEntity, Class actualType){
        super("ParentEntity from ChildEntity "+childEntity.getSimpleName()+" was of unknown Type: " + actualType.getSimpleName());
    }
}
