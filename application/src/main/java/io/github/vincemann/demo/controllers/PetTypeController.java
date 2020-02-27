package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.generic.crud.lib.config.WebComponent;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;

@WebComponent
public class PetTypeController
        extends SpringAdapterDtoCrudController<PetType, Long> {

    public PetTypeController() {
        super(DtoMappingContext.DEFAULT(PetType.class));
    }
}
