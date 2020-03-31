package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.generic.crud.lib.config.layers.component.WebController;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterJsonDtoCrudController;

@WebController
public class PetTypeController
        extends SpringAdapterJsonDtoCrudController<PetType, Long> {

    public PetTypeController() {
        super(DtoMappingContextBuilder.builder()
                .forAll(PetType.class)
                .build()
        );
    }
}
