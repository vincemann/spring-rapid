package io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler;

import io.github.vincemann.generic.crud.lib.controller.DtoCrudController;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.errorHandling.ApiError;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoSerializingException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;

//todo annotation is not inherited, so it the impl needs to be annotated
@RestControllerAdvice(assignableTypes = DtoCrudController.class)
@Slf4j
public class DtoCrudControllerExceptionHandlerImpl extends ImprovedRestExceptionHandler implements DtoCrudControllerExceptionHandler<ApiError> {


    @Override
    public ResponseEntity<ApiError> handleDtoReadingException(DtoSerializingException e) {
        logError(e);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST,
                "Invalid Dto. Cannot be read.",e));
    }

    @Override
    public ResponseEntity<ApiError> handleEntityMappingException(EntityMappingException e) {
        logError(e);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST,
                "EntityDto could not be mapped to Entity or vice versa",e));
    }

    @Override
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException e) {
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND,
                e.getLocalizedMessage(), e));
    }

    @Override
    public ResponseEntity<ApiError> handleNoIdException(NoIdException e) {
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST,
                "Id of Entity was not set, but was needed", e));
    }

    @Override
    public ResponseEntity<ApiError> handleUnknownException(Exception e) {
        logError(e);
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unknown ServerError occured in CrudController",e));
    }

    @Override
    public ResponseEntity<ApiError> handleBadEntityException(BadEntityException e) {
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST,
                "Entity sent by client was malformed",e));
    }

    protected void logError(Exception e){
        log.error("Exception caught by Handler: "+ this.getClass().getSimpleName()+ ", :",e);
    }

}
