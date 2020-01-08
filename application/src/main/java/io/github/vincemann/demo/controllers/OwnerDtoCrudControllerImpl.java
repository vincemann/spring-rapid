package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.OwnerDto;
import io.github.vincemann.generic.crud.lib.controller.DtoCrudController;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.http.ResponseEntity;

public class OwnerDtoCrudControllerImpl {

    private DtoCrudController<Long> dtoCrudController;

    public ResponseEntity<OwnerDto> findOwnerDto(Long id) throws EntityNotFoundException, NoIdException, EntityMappingException {
        return dtoCrudController.find(id);
    }



}
