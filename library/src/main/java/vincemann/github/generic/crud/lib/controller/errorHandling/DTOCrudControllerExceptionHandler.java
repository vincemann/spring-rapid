package vincemann.github.generic.crud.lib.controller.errorHandling;

import vincemann.github.generic.crud.lib.controller.exception.EntityMappingException;
import vincemann.github.generic.crud.lib.service.exception.BadEntityException;
import vincemann.github.generic.crud.lib.service.exception.EntityNotFoundException;
import vincemann.github.generic.crud.lib.service.exception.NoIdException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * handles Exceptions thrown by {@link vincemann.github.generic.crud.lib.controller.DTOCrudController}
 * Impl needs to be annotated with
 */
public interface DTOCrudControllerExceptionHandler<ExceptionResponse>{

    @ExceptionHandler(EntityMappingException.class)
    public abstract ResponseEntity<ExceptionResponse> handleEntityMappingException(EntityMappingException e);
    @ExceptionHandler(EntityNotFoundException.class)
    public abstract ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException e);
    @ExceptionHandler(NoIdException.class)
    public abstract ResponseEntity<ExceptionResponse> handleNoIdException(NoIdException e);
    @ExceptionHandler(BadEntityException.class)
    public abstract ResponseEntity<ExceptionResponse> handleBadEntityException(BadEntityException e);
    @ExceptionHandler(Exception.class)
    public abstract ResponseEntity<ExceptionResponse> handleUnknownException(Exception e);
}
