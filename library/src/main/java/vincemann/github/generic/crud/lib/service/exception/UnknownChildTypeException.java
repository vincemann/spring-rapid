package vincemann.github.generic.crud.lib.service.exception;


import vincemann.github.generic.crud.lib.model.biDir.BiDirChild;
import vincemann.github.generic.crud.lib.model.biDir.BiDirParent;
import vincemann.github.generic.crud.lib.service.springDataJpa.BiDirRelationManagingException;

public class UnknownChildTypeException extends BiDirRelationManagingException {

    public UnknownChildTypeException(Class<? extends BiDirParent> parentClass, Class<? extends BiDirChild> unknownChildClass) {
        super("ChildClass : " + unknownChildClass.getSimpleName() + " is of unknown Type for parent with type " + parentClass.getSimpleName());
    }

}
