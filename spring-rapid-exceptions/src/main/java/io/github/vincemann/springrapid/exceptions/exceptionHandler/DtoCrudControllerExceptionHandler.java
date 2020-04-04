package io.github.vincemann.springrapid.exceptions.exceptionHandler;

import io.github.vincemann.springrapid.core.controller.DtoCrudController;
import io.github.vincemann.springrapid.core.controller.dtoMapper.exception.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.springAdapter.DtoSerializingException;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.exception.NoIdException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * handles Exceptions thrown by {@link DtoCrudController}
 * Impl needs to be annotated with @RestControllerAdvice(assignableTypes = DtoCrudController.class)
 */
public interface DtoCrudControllerExceptionHandler<ExceptionResponse>{

    @ExceptionHandler(DtoMappingException.class)
    public abstract ResponseEntity<ExceptionResponse> handleEntityMappingException(DtoMappingException e);
    @ExceptionHandler(EntityNotFoundException.class)
    public abstract ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException e);
    @ExceptionHandler(NoIdException.class)
    public abstract ResponseEntity<ExceptionResponse> handleNoIdException(NoIdException e);
    @ExceptionHandler(BadEntityException.class)
    public abstract ResponseEntity<ExceptionResponse> handleBadEntityException(BadEntityException e);
    @ExceptionHandler(DtoSerializingException.class)
    public abstract ResponseEntity<ExceptionResponse> handleDtoReadingException(DtoSerializingException e);
    @ExceptionHandler(Exception.class)
    public abstract ResponseEntity<ExceptionResponse> handleUnknownException(Exception e);
}
