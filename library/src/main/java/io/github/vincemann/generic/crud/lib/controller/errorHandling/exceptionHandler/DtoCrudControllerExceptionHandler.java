package io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler;

import io.github.vincemann.generic.crud.lib.controller.DtoCrudController;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.DtoReadingException;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * handles Exceptions thrown by {@link DtoCrudController}
 * Impl needs to be annotated with @RestControllerAdvice(assignableTypes = DtoCrudController.class)
 */
public interface DtoCrudControllerExceptionHandler<ExceptionResponse>{

    @ExceptionHandler(EntityMappingException.class)
    public abstract ResponseEntity<ExceptionResponse> handleEntityMappingException(EntityMappingException e);
    @ExceptionHandler(EntityNotFoundException.class)
    public abstract ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException e);
    @ExceptionHandler(NoIdException.class)
    public abstract ResponseEntity<ExceptionResponse> handleNoIdException(NoIdException e);
    @ExceptionHandler(BadEntityException.class)
    public abstract ResponseEntity<ExceptionResponse> handleBadEntityException(BadEntityException e);
    @ExceptionHandler(DtoReadingException.class)
    public abstract ResponseEntity<ExceptionResponse> handleDtoReadingException(DtoReadingException e);
    @ExceptionHandler(Exception.class)
    public abstract ResponseEntity<ExceptionResponse> handleUnknownException(Exception e);
}
