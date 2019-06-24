package vincemann.github.generic.crud.lib.service.springDataJpa;

public class BiDirRelationManagingException extends RuntimeException{
    public BiDirRelationManagingException(String message) {
        super(message);
    }

    public BiDirRelationManagingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BiDirRelationManagingException(Throwable cause) {
        super(cause);
    }
}
